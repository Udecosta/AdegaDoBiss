package com.example.adegadobiss.constants

class Constants {
    companion object{
        const val BASE_URL: String = "https://cep.awesomeapi.com.br"
        const val lat: Double = -23.6749605
        const val lng: Double = -46.7410996

        val TIPO_DE_PAGAMENTO = arrayOf(
            "Selecione",
            "Dinheiro",
            "Sodexo VR",
            "Sodexo VA",
            "Cartão de Débito",
            "Cartão de crédito",
            "Pagamento QR Code"
        )

        val TROCO = arrayOf(
            "Não Precisa de Troco",
            "R$ 20,00",
            "R$ 50,00",
            "R$ 100,00"
        )
    }

}