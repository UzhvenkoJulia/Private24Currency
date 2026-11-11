package com.example.private24.data.remote

import com.example.private24.data.model.ExchangeArchiveResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivatArchiveApi {
    @GET("exchange_rates?json")
    fun getExchangeRates(
        @Query("date") date: String
    ): Call<ExchangeArchiveResponse?>?
}