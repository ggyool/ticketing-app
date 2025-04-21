package com.ggyool.ticketing.application.worker

import java.time.LocalDateTime

data class EventChangeEvent(
    val eventType: String,
    val aggregate: String,
    val timestamp: LocalDateTime,
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