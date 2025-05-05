package com.ggyool.event.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EventDomainEventListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenEventDomainEvent(eventDomainEvent: EventDomainEvent) {
        kafkaTemplate.send(
            "domain.event",
            eventDomainEvent.aggregateId.toString(),
            objectMapper.writeValueAsString(eventDomainEvent),
        )
    }
}
