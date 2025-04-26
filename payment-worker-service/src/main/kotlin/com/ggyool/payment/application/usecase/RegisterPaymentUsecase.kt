package com.ggyool.payment.application.usecase

interface RegisterPaymentUsecase {

    fun registerPayment(
        eventId: Long,
        userId: Long,
        ticketId: Long,
    ): Long
}