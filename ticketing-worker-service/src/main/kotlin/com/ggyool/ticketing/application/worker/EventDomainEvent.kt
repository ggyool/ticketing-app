package com.ggyool.ticketing.application.worker

import com.ggyool.common.event.DomainEvent
import com.ggyool.ticketing.repository.entity.EventLogEntity
import java.time.LocalDateTime
import java.util.*

data class EventDomainEvent(
    override val eventId: String,
    override val version: Long,
    override val aggregateId: Long,
    override val aggregateType: String,
    override val eventType: String,
    override val timeStamp: LocalDateTime,
    override val payload: Payload,
) : DomainEvent<Long, EventDomainEvent.Payload> {

    fun toEventLogEntity(): EventLogEntity {
        return EventLogEntity(
            eventId = UUID.fromString(eventId),
            aggregateType = aggregateType,
            aggregateId = aggregateId.toString(),
            version = version,
            timestamp = timeStamp,
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
