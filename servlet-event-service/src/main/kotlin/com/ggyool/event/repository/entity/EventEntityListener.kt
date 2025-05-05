package com.ggyool.event.repository.entity

import com.ggyool.event.application.event.EventDomainEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class EventEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(eventEntity: EventEntity) {
        applicationEventPublisher.publishEvent(
            EventDomainEvent(
                eventId = UUID.randomUUID().toString(),
                version = eventEntity.version,
                aggregateId = eventEntity.id!!,
                aggregateType = "event",
                eventType = "create",
                timeStamp = LocalDateTime.now(),
                payload = EventDomainEvent.Payload.from(eventEntity)
            )
        )
    }

    @PostUpdate
    fun postUpdate(eventEntity: EventEntity) {
        applicationEventPublisher.publishEvent(
            EventDomainEvent(
                eventId = UUID.randomUUID().toString(),
                version = eventEntity.version!!,
                aggregateId = eventEntity.id!!,
                aggregateType = "event",
                eventType = "update",
                timeStamp = LocalDateTime.now(),
                payload = EventDomainEvent.Payload.from(eventEntity)
            )
        )
    }
}