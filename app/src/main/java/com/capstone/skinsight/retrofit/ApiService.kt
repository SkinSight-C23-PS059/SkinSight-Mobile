package com.capstone.skinsight.retrofit

import com.capstone.skinsight.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @Multipart
    @POST("predict")
    fun uploadImage(
        @Part file : MultipartBody.Part
    ): Call<UploadResponse>
}