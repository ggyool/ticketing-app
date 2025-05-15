package com.ggyool.event.application.event

import com.ggyool.common.event.DomainEvent
import com.ggyool.event.repository.entity.EventEntity
import java.time.LocalDateTime

data class EventDomainEvent(
    override val eventId: String,
    override val version: Long?,
    override val aggregateId: Long,
    override val aggregateType: String,
    override val eventType: String,
    override val timeStamp: LocalDateTime,
    override val payload: Payload,
) : DomainEvent<Long, EventDomainEvent.Payload> {

    data class Payload(
        val id: Long,
        val name: String,
        val ticketQuantity: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    ) {

        companion object {
            fun from(entity: EventEntity): Payload {
                return Payload(
                    id = entity.id!!,
                    name = entity.name,
                    ticketQuantity = entity.ticketQuantity,
                    createdAt = entity.createdAt!!,
                    updatedAt = entity.updatedAt!!,
                )
            }
        }
    }
}