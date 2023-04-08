package com.example.adegadobiss.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    var produto: String = "",
    var quantidade: Int = 1,
    var valor: Double = 0.00,
    var imagem: String = "",
    var subtotal: Double = 0.00,
    var total: Double = 0.00,
    val categoria: String = "",
    val descricao: String = "",
    val conteudo: String = "",
    val estoque : Boolean = true
): Parcelable