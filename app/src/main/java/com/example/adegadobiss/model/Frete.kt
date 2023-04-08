package com.example.adegadobiss.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Frete (
    var frete: Double = 0.00
): Parcelable