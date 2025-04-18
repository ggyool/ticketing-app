package com.ggyool.event.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EventDomainEventListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenEventCreateEvent(eventCreateEvent: EventCreateEvent) {
        // TODO: 커밋후에 실행되므로 rdb에 누락될 일은 없지만 produce에 실패하는 케이스 고려가 안되어 있다.
        // 아웃박스 패턴이나 produce dead letter를 도입해야함
        kafkaTemplate.send(
            "event.create",
            eventCreateEvent.payload.id.toString(),
            objectMapper.writeValueAsString(eventCreateEvent),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenEventUpdateEvent(eventUpdateEvent: EventUpdateEvent) {
        // TODO: 위와 동일
        kafkaTemplate.send(
            "event.update",
            eventUpdateEvent.payload.id.toString(),
            objectMapper.writeValueAsString(eventUpdateEvent),
        )
    }
}