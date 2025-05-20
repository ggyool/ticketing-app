package com.ggyool.payment.application.usecase

import java.util.*

interface FailPaymentUsecase {

    fun failPayment(
        paymentId: UUID,
        pgPaymentId: String,
    )
}