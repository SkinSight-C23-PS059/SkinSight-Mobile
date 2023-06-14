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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.skinsight.CameraActivity
import com.capstone.skinsight.R
import com.capstone.skinsight.databinding.FragmentScanxBinding
import com.capstone.skinsight.retrofit.ApiConfig
import com.capstone.skinsight.retrofit.UploadResponse
import com.capstone.skinsight.rotateBitmap
import com.capstone.skinsight.uriToFile
import okhttp3.MediaType.Companion.toMediaType
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
                    "Permission denied",
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
        val chooser = Intent.createChooser(intent, "Choose image")
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

    private fun uploadImage() {
        if (getFile != null) {
            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val service = ApiConfig.getApiService().uploadImage(imageMultipart)
            service.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val uploadResponse = response.body()
                        if (uploadResponse != null) {
                            val message = uploadResponse.description
                            Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                            println(response.body())
                            requireActivity().finish()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Upload failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        println(response)
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Connection failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                requireContext(),
                "Insert picture",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
