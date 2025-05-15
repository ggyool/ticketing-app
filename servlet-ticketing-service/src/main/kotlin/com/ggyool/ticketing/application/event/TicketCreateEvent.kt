package com.ggyool.ticketing.application.event

import java.time.LocalDateTime

// TODO 추후 evnet-service 와 같은 구조로 ticket 도메인 이벤트로 수정
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
