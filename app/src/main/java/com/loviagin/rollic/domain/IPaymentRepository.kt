package com.loviagin.rollic.domain

import com.loviagin.rollic.data.dto.Response
import kotlinx.coroutines.flow.Flow
import ru.yoomoney.sdk.kassa.payments.TokenizationResult
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount

interface IPaymentRepository {

    fun createPayment(amount: Amount, tokenizationResult: TokenizationResult): Flow<Response>
    fun getPaymentStatus(id: String): Flow<Response>
}