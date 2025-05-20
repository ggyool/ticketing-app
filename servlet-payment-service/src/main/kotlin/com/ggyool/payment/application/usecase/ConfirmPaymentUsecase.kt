package com.ggyool.payment.application.usecase

interface ConfirmPaymentUsecase {

    fun confirmPayment(confirmPaymentInput: ConfirmPaymentInput): ConfirmPaymentOutput

    data class ConfirmPaymentInput(
        val paymentId: String,
        val pgPaymentId: String,
        val amount: Long
    )

    data class ConfirmPaymentOutput(
        val paymentId: String,
        val pgPaymentId: String,
        val succeeded: Boolean,
    )
}