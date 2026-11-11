package com.example.private24.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PrivatArchiveNetworkModule {
    private val BASE_URL = "https://api.privatbank.ua/p24api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PrivatArchiveApi = retrofit.create(PrivatArchiveApi::class.java)
}