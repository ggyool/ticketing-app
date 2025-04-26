package com.ggyool.ticketing.repository.entity

import com.ggyool.ticketing.application.event.TicketCreateEvent
import jakarta.persistence.PostPersist
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class TicketEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(ticketEntity: TicketEntity) {
        applicationEventPublisher.publishEvent(
            TicketCreateEvent(
                payload = TicketCreateEvent.Payload(
                    id = ticketEntity.id!!,
                    eventId = ticketEntity.eventId,
                    userId = ticketEntity.userId,
                    reservedAt = ticketEntity.reservedAt,
                    version = ticketEntity.version!!,
                    createdAt = ticketEntity.createdAt!!,
                    updatedAt = ticketEntity.updatedAt!!
                )
            )
        )
    }
}