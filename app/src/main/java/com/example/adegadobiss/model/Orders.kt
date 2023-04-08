package com.example.adegadobiss.model

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class Orders(
    var nome: String = "",
    var telefone: String = "",
    var cep: String = "",
    var endereco: String = "",
    var bairro: String = "",
    var complemento: String = "",
    var numero: String = "",
    var produtos: MutableList<Product> = arrayListOf(),
    var numPedido: Int = 0,
    var data: String = "",
    var formaDePagamento : String = "",
    val valorTotal : Double = 0.00,
    val frete : Double = 0.00
    ) : Parcelable