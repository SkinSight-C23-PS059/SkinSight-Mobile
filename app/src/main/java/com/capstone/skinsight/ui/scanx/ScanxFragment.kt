package com.capstone.skinsight.ui.scanx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.skinsight.*
import com.capstone.skinsight.databinding.FragmentScanxBinding
import com.capstone.skinsight.retrofit.ApiConfig
import com.capstone.skinsight.retrofit.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanxFragment : Fragment() {

    private var _binding: FragmentScanxBinding? = null
    private val binding get() = _binding!!
    private var getFile: File? = null
    private lateinit var uploadViewModel: ScanxViewModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    requireContext(),
                    "Izin ditolak",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanxBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        uploadViewModel = ViewModelProvider(this).get(ScanxViewModel::class.java)
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.btnScan.setOnClickListener { uploadImage() }

        uploadViewModel.imagePreview.observe(viewLifecycleOwner) { photo ->
            binding.previewImageView.setImageBitmap(photo)
        }

        return root
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri? = result.data?.data
            val myFile = selectedImg?.let { uriToFile(it, requireContext()) }

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = result.data?.getSerializableExtra("picture") as? File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) ?: true

            getFile = myFile

            val result = getFile?.let {
                rotateBitmap(
                    BitmapFactory.decodeFile(it.path),
                    isBackCamera
                )
            }
            result?.let { uploadViewModel.getPhoto(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun uploadImage() {
        val file = reduceFileImage(getFile as File)
        val reqImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            reqImage
        )
        showLoading(true)
        val service = ApiConfig.getApiService().uploadImage(imageMultipart)
        service.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        alertShow(true, responseBody)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        (getString(R.string.txtuploadfail)),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    (getString(R.string.txtuploadfail)),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun alertShow(alertShow: Boolean, responseBody: UploadResponse?) {
        if (alertShow) {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle(getString(R.string.upload_response))
            val message =
                        "Name: ${responseBody?.name}\n" +
                        "Severity: ${responseBody?.severity}\n" +
                        "Description: ${responseBody?.description}\n" +
                        "Confidence: ${responseBody?.confidence}\n" +
                        "Action: ${responseBody?.action}"
            dialogBuilder.setMessage(message)
            dialogBuilder.setPositiveButton(getString(R.string.close)) { _, _ -> }
            dialogBuilder.create().show()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
