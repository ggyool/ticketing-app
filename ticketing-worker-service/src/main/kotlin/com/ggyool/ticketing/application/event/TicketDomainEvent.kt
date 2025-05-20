package com.ggyool.ticketing.application.event

import com.ggyool.common.event.DomainEvent
import com.ggyool.ticketing.application.event.TicketDomainEvent.Payload
import com.ggyool.ticketing.repository.entity.TicketEntity
import com.ggyool.ticketing.repository.entity.TicketStatus
import java.time.LocalDateTime
import java.util.*

class TicketDomainEvent(
    override val eventId: String,
    override val version: Long?,
    override val aggregateId: UUID,
    override val aggregateType: String,
    override val eventType: String,
    override val timeStamp: LocalDateTime,
    override val payload: Payload,
) : DomainEvent<UUID, Payload> {

    data class Payload(
        val id: UUID,
        val eventId: Long,
        val userId: Long,
        val status: TicketStatus,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    ) {

        companion object {
            fun from(entity: TicketEntity): Payload {
                return Payload(
                    id = entity.id,
                    eventId = entity.eventId,
                    userId = entity.userId,
                    status = entity.status,
                    createdAt = entity.createdAt!!,
                    updatedAt = entity.updatedAt!!,
                )
            }
        }
    }
}