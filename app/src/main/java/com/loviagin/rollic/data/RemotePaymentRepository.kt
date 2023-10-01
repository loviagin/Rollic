package com.loviagin.rollic.data

import com.loviagin.rollic.data.dto.CreatePayment
import com.loviagin.rollic.data.dto.Response
import com.loviagin.rollic.domain.IPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.yoomoney.sdk.kassa.payments.TokenizationResult
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount

class RemotePaymentRepository(private val paymentApi: PaymentApi) : IPaymentRepository {

    override fun createPayment(
        amount: Amount,
        tokenizationResult: TokenizationResult
    ): Flow<Response> {
        return flow {
            val response = paymentApi.createPayment(
                createPayment = CreatePayment(
                    amount = amount.value,
                    currency = amount.currency.currencyCode,
                    method = tokenizationResult.paymentMethodType.name.lowercase(),
                    token = tokenizationResult.paymentToken
                )
            )

            if (!response.success) {
                throw NetworkException(response.message)
            }
            emit(response)
        }
    }

    override fun getPaymentStatus(id: String): Flow<Response> {
        return flow {

            val response = paymentApi.getPayment(id)

            if (!response.success) {
                throw NetworkException(response.message)
            }
            emit(response)
        }
    }
}