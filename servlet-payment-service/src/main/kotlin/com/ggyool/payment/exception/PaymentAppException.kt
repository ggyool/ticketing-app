package com.ggyool.payment.exception

import org.springframework.http.HttpStatus

class PaymentAppException(
    val httpStatus: HttpStatus,
    val reason: String,
) : RuntimeException(reason)