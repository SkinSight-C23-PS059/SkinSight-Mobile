package com.capstone.skinsight.retrofit

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @field:SerializedName("severity")
    val severity: String,

    @field:SerializedName("confidence")
    val confidence: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("action")
    val action: String,

    @field:SerializedName("description")
    val description: String
)
