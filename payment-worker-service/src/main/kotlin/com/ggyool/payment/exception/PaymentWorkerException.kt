package com.ggyool.payment.exception

class PaymentWorkerException(
    val reason: String,
) : RuntimeException(reason)