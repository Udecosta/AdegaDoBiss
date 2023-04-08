package com.example.adegadobiss.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    var category: String,
) : Parcelable