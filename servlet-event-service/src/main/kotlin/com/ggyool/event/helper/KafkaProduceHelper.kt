package com.ggyool.event.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.event.application.producer.DeadLetterKafkaProducer
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

fun produceWithDlt(
    topic: String,
    key: String,
    payload: Any,
) {
    try {
        kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload))
    } catch (ex: Exception) {
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