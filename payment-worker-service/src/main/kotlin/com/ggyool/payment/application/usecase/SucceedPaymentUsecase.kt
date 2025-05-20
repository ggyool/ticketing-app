package com.ggyool.payment.application.usecase

import java.util.*

interface SucceedPaymentUsecase {

    fun succeedPayment(
        paymentId: UUID,
        pgPaymentId: String,
    )
}