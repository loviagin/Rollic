package com.loviagin.rollic.domain.usecase

import com.loviagin.rollic.domain.IPaymentRepository
import ru.yoomoney.sdk.kassa.payments.TokenizationResult
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import javax.inject.Inject

class GetPaymentUseCase @Inject constructor(
    private val paymentRepository: IPaymentRepository
) {
    operator fun invoke(id: String) =
        paymentRepository.getPaymentStatus(id)
}