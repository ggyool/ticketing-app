package com.ggyool.event.application.event

import java.time.LocalDateTime

data class EventCreateEvent(
    val eventType: String = "EventCreateEvent",
    val aggregate: String = "Event",
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val payload: Payload,
) {

    data class Payload(
        val id: Long,
        val name: String,
        val ticketQuantity: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}