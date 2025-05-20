package com.ggyool.payment.controller

import com.ggyool.payment.application.usecase.ConfirmPaymentUsecase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/payment")
@RestController
class PaymentController(
    private val confirmPaymentUsecase: ConfirmPaymentUsecase
) {

    @PostMapping("/confirm")
    fun confirmPayment(
        @RequestBody confirmPaymentInput: ConfirmPaymentUsecase.ConfirmPaymentInput
    ) = confirmPaymentUsecase.confirmPayment(confirmPaymentInput)
}