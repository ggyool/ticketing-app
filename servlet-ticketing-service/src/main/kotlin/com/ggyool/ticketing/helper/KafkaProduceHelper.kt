package com.ggyool.ticketing.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.ticketing.application.producer.DeadLetterKafkaProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

@Suppress("UNCHECKED_CAST")
private val kafkaTemplate: KafkaTemplate<String, String> by lazy {
    ApplicationContextProvider.getBean(KafkaTemplate::class.java) as KafkaTemplate<String, String>
}

private val deadLetterKafkaProducer: DeadLetterKafkaProducer by lazy {
    ApplicationContextProvider.getBean(DeadLetterKafkaProducer::class.java)
}

private val objectMapper: ObjectMapper by lazy {
    ApplicationContextProvider.getBean(ObjectMapper::class.java)
}

private val logger: Logger = LoggerFactory.getLogger("KafkaProduceHelper")

fun produceWithDlt(
    topic: String,
    key: String,
    payload: Any,
) {
    try {
        if (payload is String) {
            kafkaTemplate.send(topic, key, payload)
        } else {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload))
        }
    } catch (ex: Exception) {
        logger.error("[$topic] Producing Error (key: $key, payload: $payload}", ex)
        deadLetterKafkaProducer.send(
            DeadLetterEvent(
                reason = ex.javaClass.name,
                reissuedTopic = topic,
                reissuedKey = key,
                reissuedPayload = objectMapper.writeValueAsString(payload)
            )
        )
    }
}