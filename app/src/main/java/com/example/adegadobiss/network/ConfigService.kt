package com.example.adegadobiss.network

import com.example.adegadobiss.constants.Constants
import com.example.adegadobiss.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConfigService {
    fun getInstance(): ApiService? {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            return retrofit.create(ApiService::class.java)
    }
}