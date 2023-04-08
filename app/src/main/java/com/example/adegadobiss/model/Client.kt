package com.example.adegadobiss.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class Client(
    var nome: String = "",
    var telefone: String = "",
    var cep: String = "",
    var endereco: String = "",
    var bairro: String = "",
    var complemento: String = "",
    var numero: String = ""
    ) : Parcelable
