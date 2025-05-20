package com.ggyool.payment.application.usecase

import java.util.*

interface RegisterPaymentUsecase {

    fun registerPayment(
        registerPaymentInput: RegisterPaymentInput
    ): UUID

    data class RegisterPaymentInput(
        val eventId: Long,
        val userId: Long,
        val ticketId: String,
    )
}