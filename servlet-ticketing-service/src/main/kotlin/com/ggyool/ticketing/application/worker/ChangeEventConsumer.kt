package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
        topics = [CREATE_EVENT_TOPIC, UPDATE_EVENT_TOPIC],
        containerFactory = "eventKafkaListenerContainerFactory",
        groupId = "change-event-group",
        concurrency = "2"
    )
    fun listen(record: ConsumerRecord<String, String>) {
        val eventId = record.key();
        val event = objectMapper.readValue<EventChangeEvent>(record.value())
        stringRedisTemplate.opsForValue().set(
            TICKETING_QUANTITY_KEY.format(eventId),
            event.payload.ticketQuantity.toString()
        )
    }

    companion object {
        private const val CREATE_EVENT_TOPIC: String = "event.create"
        private const val UPDATE_EVENT_TOPIC: String = "event.update"
        private const val TICKETING_QUANTITY_KEY: String = "ticketing.quantity.%s"
    }
}