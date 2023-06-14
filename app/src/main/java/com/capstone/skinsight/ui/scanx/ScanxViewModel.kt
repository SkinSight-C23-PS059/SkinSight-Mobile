package com.capstone.skinsight.ui.scanx

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanxViewModel : ViewModel() {

    private val _imagePreview = MutableLiveData<Bitmap>()
    val imagePreview: LiveData<Bitmap> = _imagePreview

    fun getPhoto(photo: Bitmap) {
        _imagePreview.postValue(photo)
    }
}