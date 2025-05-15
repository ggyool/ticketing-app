package com.ggyool.user.application.helper

import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.user.application.producer.DeadLetterKafkaProducer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.support.Acknowledgment
import java.util.*

inline fun consumeWithDlt(
    record: ConsumerRecord<String, String>,
    acknowledgment: Acknowledgment,
    deadLetterKafkaProducer: DeadLetterKafkaProducer,
    block: () -> Unit,
) {
    try {
        block()
    } catch (ex: Exception) {
        deadLetterKafkaProducer.send(
            DeadLetterEvent(
                eventId = UUID.fromString(record.key()),
                reason = ex.javaClass.name,
                reissuedTopic = record.topic(),
                reissuedKey = record.key(),
                reissuedPayload = record.value()
            )
        )
    } finally {
        acknowledgment.acknowledge()
    }
}