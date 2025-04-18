package com.ggyool.event.repository.entity

import com.ggyool.event.application.event.EventCreateEvent
import com.ggyool.event.application.event.EventUpdateEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(eventEntity: EventEntity) {
        applicationEventPublisher.publishEvent(
            EventCreateEvent(
                payload = EventCreateEvent.Payload(
                    id = eventEntity.id!!,
                    name = eventEntity.name,
                    ticketQuantity = eventEntity.ticketQuantity,
                    createdAt = eventEntity.createdAt!!,
                    updatedAt = eventEntity.updatedAt!!
                )
            )
        )
    }

    @PostUpdate
    fun postUpdate(eventEntity: EventEntity) {
        applicationEventPublisher.publishEvent(
            EventUpdateEvent(
                payload = EventUpdateEvent.Payload(
                    id = eventEntity.id!!,
                    name = eventEntity.name,
                    ticketQuantity = eventEntity.ticketQuantity,
                    createdAt = eventEntity.createdAt!!,
                    updatedAt = eventEntity.updatedAt!!
                )
            )
        )
    }
}