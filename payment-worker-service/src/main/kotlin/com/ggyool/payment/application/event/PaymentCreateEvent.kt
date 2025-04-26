package com.ggyool.payment.application.event

import java.time.LocalDateTime

data class PaymentCreateEvent(
    val eventType: String = "PaymentCreateEvent",
    val aggregate: String = "Payment",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val payload: Payload,
) {

    data class Payload(
        val id: Long,
        val eventId: Long,
        val userId: Long,
        val status: String,
        val version: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
