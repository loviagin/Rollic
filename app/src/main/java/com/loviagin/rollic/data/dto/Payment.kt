package com.loviagin.rollic.data.dto

import com.google.gson.annotations.SerializedName
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType

data class Payment(
    @SerializedName("id")
    val id: String = "1",
    @SerializedName("confirmationUrl")
    val confirmationUrl: String?,
    @SerializedName("confirmationType")
    val confirmationType: String?,
    @SerializedName("methodType")
    val methodType: PaymentMethodType,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("test")
    val test: Boolean = true,
    @SerializedName("paid")
    val paid: Boolean = true
)
