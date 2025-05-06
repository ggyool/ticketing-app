package com.ggyool.ticketing.application.worker

import com.ggyool.ticketing.repository.entity.EventLogEntity
import java.time.LocalDateTime
import java.util.*

data class EventDomainEvent(
    val eventId: String,
    val version: Long,
    val aggregateId: Long,
    val aggregateType: String,
    val eventType: String,
    val timeStamp: LocalDateTime,
    val payload: Payload,
) {

    fun toEventLogEntity(): EventLogEntity {
        return EventLogEntity(
            eventId = UUID.fromString(eventId),
            aggregateType = aggregateType,
            aggregateId = aggregateId.toString(),
            version = version,
            timeStamp = timeStamp,
        )
    }

    data class Payload(
        val id: Long,
        val name: String,
        val ticketQuantity: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}
