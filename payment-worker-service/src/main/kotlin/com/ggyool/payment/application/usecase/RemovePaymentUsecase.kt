package com.ggyool.payment.application.usecase

import java.util.*

interface RemovePaymentUsecase {

    fun removePayment(
        paymentId: UUID
    )
}