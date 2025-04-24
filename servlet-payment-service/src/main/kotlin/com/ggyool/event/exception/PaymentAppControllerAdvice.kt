package com.ggyool.event.exception

import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class PaymentAppControllerAdvice {

    @ExceptionHandler
    fun handlePaymentAppException(ex: PaymentAppException) =
        ErrorResponse.builder(ex, ex.httpStatus, ex.reason).build()
}