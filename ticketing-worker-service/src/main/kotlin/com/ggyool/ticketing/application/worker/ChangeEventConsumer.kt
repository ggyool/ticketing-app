package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.ticketing.exception.TicketingWorkerException
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ChangeEventConsumer(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [CREATE_EVENT_TOPIC],
        containerFactory = "eventKafkaListenerContainerFactory",
        groupId = "change-event-group",
        concurrency = "2"
    )
    fun listenCreateEvent(record: ConsumerRecord<String, String>) {
        val eventId = record.key();
        val event = objectMapper.readValue<EventChangeEvent>(record.value())
        stringRedisTemplate.opsForValue().set(
            TICKETING_QUANTITY_KEY.format(eventId),
            event.payload.ticketQuantity.toString()
        )
    }

    @KafkaListener(
        topics = [UPDATE_EVENT_TOPIC],
        containerFactory = "eventKafkaListenerContainerFactory",
        groupId = "change-event-group",
        concurrency = "2"
    )
    fun listenUpdateEvent(record: ConsumerRecord<String, String>) {
        val eventId = record.key()
        val event = objectMapper.readValue<EventChangeEvent>(record.value())
        val opsForValue = stringRedisTemplate.opsForValue()
        val redisKey = TICKETING_QUANTITY_KEY.format(eventId)
        // TODO: MEMO
        // 해당 워커가 돌지 않고 있는 상황에서 create 와 update 토픽에 메시지가 쌓여있는 상황에서 update 가 먼저 실행될 수 있다.
        // update 는 키가 존재하지 않으면 실행하지 않는다.
        // 지금은 단순 예외를 발생시켰지만 어딘가에 쌓아서 재처리되게끔 해야 한다
        if (!stringRedisTemplate.hasKey(redisKey)) {
            throw TicketingWorkerException("redis key가 존재하지 않습니다: $redisKey")
        }
        opsForValue.set(
            redisKey,
            event.payload.ticketQuantity.toString()
        )
    }

    companion object {
        private const val CREATE_EVENT_TOPIC: String = "event.create"
        private const val UPDATE_EVENT_TOPIC: String = "event.update"
        private const val TICKETING_QUANTITY_KEY: String = "ticketing.quantity.%s"
    }
}