package com.example.adegadobiss.service

import com.example.adegadobiss.model.Client
import com.example.adegadobiss.model.Endereco
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/json/{cep}")
    fun getCep(@Path("cep") cep: String): Call<Endereco>
}