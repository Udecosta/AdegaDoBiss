package com.example.adegadobiss.model

class Endereco {

    var cep: String? = ""
    var address: String? = ""
    var complemento: String? = ""
    var district: String? = ""
    var localidade: String? = ""
    var uf: String? = ""
    var unidade: String? = ""
    var ibge: String? = ""
    var gia: String? = ""
    var lat: Double? = 0.00
    var lng: Double? = 0.00

   override fun toString(): String {
        return "cep $cep logradouro $address complemento $complemento bairro $district localidade $localidade uf $uf unidade $unidade ibge $ibge gia $gia lat $lat lng $lng"
    }
}
