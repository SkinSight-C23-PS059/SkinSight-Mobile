package com.capstone.skinsight

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article(
    val title: String,
    val description: String,
    val photo: Int,
): Parcelable