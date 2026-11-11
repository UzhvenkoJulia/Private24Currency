package com.example.private24.data.model

import com.google.gson.annotations.SerializedName

data class ExchangeArchiveResponse(
    @SerializedName("date")
    val date: String?,

    @SerializedName("bank")
    val bank: String?,

    @SerializedName("baseCurrency")
    val baseCurrency: Int?,

    @SerializedName("baseCurrencyLit")
    val baseCurrencyLit: String?,

    @SerializedName("exchangeRate")
    val exchangeRate: List<ExchangeRateItem>?
)

data class ExchangeRateItem(
    @SerializedName("baseCurrency")
    val baseCurrency: String?,

    @SerializedName("currency")
    val currency: String?,

    @SerializedName("purchaseRateNB")
    val purchaseRateNB: Double?,

    @SerializedName("saleRateNB")
    val saleRateNB: Double?,

    @SerializedName("purchaseRate")
    val purchaseRate: Double?,

    @SerializedName("saleRate")
    val saleRate: Double?
)