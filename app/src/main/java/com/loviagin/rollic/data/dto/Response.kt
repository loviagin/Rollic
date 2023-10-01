package com.loviagin.rollic.data.dto

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val payment: Payment
)