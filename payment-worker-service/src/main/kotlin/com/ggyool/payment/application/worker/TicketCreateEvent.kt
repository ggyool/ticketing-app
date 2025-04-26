package com.ggyool.payment.application.worker

import java.time.LocalDateTime

data class TicketCreateEvent(
    val eventType: String,
    val aggregate: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val payload: Payload,
) {
    data class Payload(
        val id: Long,
        val eventId: Long,
        val userId: Long,
        val ticketId: Long,
        val reservedAt: LocalDateTime,
        val version: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
