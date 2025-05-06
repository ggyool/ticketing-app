package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.ticketing.repository.EventLogJpaRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class EventDomainEventConsumer(
    private val stringRedisTemplate: StringRedisTemplate,
    private val eventLogJpaRepository: EventLogJpaRepository,
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
        val id = record.key()
        val event = objectMapper.readValue<EventDomainEvent>(record.value())
        val eventId = event.eventId
        val eventType = event.eventType

        // 처리하려는 이벤트 타입이 아닌경우
        if (eventType != "create" && eventType != "update") {
            // TODO acknowledge.acknowledge() 계속 넣어야하는 구조 변경
            acknowledge.acknowledge()
            return
        }

        // 중복 메시지 확인
        val eventLog = eventLogJpaRepository.findByIdOrNull(UUID.fromString(eventId))
        if (eventLog != null) {
            logger.info("[eventId: $eventId] 중복 메시지 입니다. ($event)")
            acknowledge.acknowledge()
            return
        }

        // 확실한 순서 체크를 위해 버전 체크 도입
        // ex. create와 update가 거의 동시에 발생해서 update 콘슈밍을 먼저 함
        if (event.version != 0L) {
            val previousVersionEventLog = eventLogJpaRepository.findEventLog(
                event.aggregateType,
                event.aggregateId.toString(),
                event.version - 1
            )
            if (previousVersionEventLog == null) {
                // TODO: DLT에 넣어서 나중에 처리도도록 해야함
                acknowledge.acknowledge()
                return
            }
        }

        // TODO: 아래 3개를 원자적으로 묶어야 함
        eventLogJpaRepository.save(event.toEventLogEntity())

        stringRedisTemplate.opsForValue().set(
            TICKETING_QUANTITY_KEY.format(id),
            event.payload.ticketQuantity.toString()
        )

        acknowledge.acknowledge()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_DOMAIN_EVENT_TOPIC: String = "domain.event"
        private const val TICKETING_QUANTITY_KEY: String = "ticketing:quantity:%s"
    }
}