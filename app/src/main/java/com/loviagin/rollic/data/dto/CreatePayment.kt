package com.loviagin.rollic.data.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CreatePayment(
    @SerializedName("amount_value")
    private val amount: BigDecimal = BigDecimal.ZERO,
    @SerializedName("amount_currency")
    private val currency: String = "RUB",
    @SerializedName("token")
    private val token: String = "",
    @SerializedName("payment_method")
    private val method: String = "",
)