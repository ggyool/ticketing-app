package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.ticketing.application.producer.DeadLetterKafkaProducer
import com.ggyool.ticketing.application.service.EventCheckService
import com.ggyool.ticketing.application.service.TicketingUpdateService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class EventDomainEventConsumer(
    private val ticketingUpdateService: TicketingUpdateService,
    private val eventCheckService: EventCheckService,
    private val deadLetterKafkaProducer: DeadLetterKafkaProducer,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [EVENT_DOMAIN_EVENT_TOPIC],
        containerFactory = "eventKafkaListenerContainerFactory",
        groupId = "event-domain-event-group",
        concurrency = "2"
    )
    fun listenEventDomainEvent(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) {
        val event = objectMapper.readValue<EventDomainEvent>(record.value())
        val eventId = event.eventId
        val eventType = event.eventType

        try {
            // 처리하려는 이벤트 타입이 아닌경우
            if (eventType != "create" && eventType != "update") {
                return
            }
            // 중복 메시지 확인
            if (eventCheckService.isDuplicated(eventId)) {
                logger.info("[eventId: $eventId] 중복 메시지 입니다. ($event)")
                return
            }
            // 연관된 도메인의 과거 이벤트가 처리 되지 않은 상황에서 이벤트가 들어온 경우를 체크
            if (!eventCheckService.isValidOrder(event)) {
                deadLetterKafkaProducer.send(
                    DeadLetterEvent(
                        reason = "INVALID_ORDER",
                        reissuedTopic = record.topic(),
                        reissuedKey = record.key(),
                        reissuedPayload = record.value()
                    )
                )
                return
            }
            val isSucceed = ticketingUpdateService.updateTicket(event)
            if (!isSucceed) {
                deadLetterKafkaProducer.send(
                    DeadLetterEvent(
                        reason = "OPERATION_FAIL",
                        reissuedTopic = record.topic(),
                        reissuedKey = record.key(),
                        reissuedPayload = record.value()
                    )
                )
            }
        } finally {
            acknowledge.acknowledge()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_DOMAIN_EVENT_TOPIC: String = "domain.event"
    }
}