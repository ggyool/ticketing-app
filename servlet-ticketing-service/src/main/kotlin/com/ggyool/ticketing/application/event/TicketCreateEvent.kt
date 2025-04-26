package com.ggyool.ticketing.application.event

import java.time.LocalDateTime

data class TicketCreateEvent(
    val eventType: String = "TicketCreateEvent",
    val aggregate: String = "Ticket",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val payload: Payload,
) {
    data class Payload(
        val id: Long,
        val eventId: Long,
        val userId: Long,
        val reservedAt: LocalDateTime,
        val version: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
