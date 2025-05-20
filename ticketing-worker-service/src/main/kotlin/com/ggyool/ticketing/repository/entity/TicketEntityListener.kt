package com.ggyool.ticketing.repository.entity

import com.ggyool.ticketing.application.event.TicketDomainEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class TicketEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(ticketEntity: TicketEntity) {
        applicationEventPublisher.publishEvent(
            TicketDomainEvent(
                UUID.randomUUID().toString(),
                version = ticketEntity.version!!,
                aggregateId = ticketEntity.id,
                aggregateType = "ticket",
                eventType = "create",
                timeStamp = LocalDateTime.now(),
                payload = TicketDomainEvent.Payload.from(ticketEntity)
            )
        )
    }

    @PostUpdate
    fun postUpdate(ticketEntity: TicketEntity) {
        applicationEventPublisher.publishEvent(
            TicketDomainEvent(
                UUID.randomUUID().toString(),
                version = ticketEntity.version!!,
                aggregateId = ticketEntity.id,
                aggregateType = "ticket",
                eventType = "update",
                timeStamp = LocalDateTime.now(),
                payload = TicketDomainEvent.Payload.from(ticketEntity)
            )
        )
    }
}