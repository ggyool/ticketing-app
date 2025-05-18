package com.ggyool.payment.helper

import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.payment.application.producer.DeadLetterKafkaProducer
import com.ggyool.payment.repository.EventLogJpaRepository
import com.ggyool.payment.repository.entity.EventLogEntity
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import java.util.*

private val deadLetterKafkaProducer: DeadLetterKafkaProducer by lazy {
    ApplicationContextProvider.getBean(DeadLetterKafkaProducer::class.java)
}

private val eventLogJpaRepository: EventLogJpaRepository by lazy {
    ApplicationContextProvider.getBean(EventLogJpaRepository::class.java)
}

private val logger: Logger = LoggerFactory.getLogger("KafkaConsumeHelper")

fun consumeWithDlt(
    record: ConsumerRecord<String, String>,
    acknowledgment: Acknowledgment,
    block: () -> Unit,
) {
    val eventId = UUID.fromString(record.key())
    try {
        if (eventLogJpaRepository.existsById(eventId)) {
            logger.info("[eventId: $eventId] 중복 메시지 입니다. ($record)")
            return
        }
        eventLogJpaRepository.save(EventLogEntity(eventId))
        block()
    } catch (ex: Exception) {
        logger.error("${record.key()} Consuming Error $record", ex)
        deleteEventLog(eventId)
        deadLetterKafkaProducer.send(
            DeadLetterEvent(
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

private fun deleteEventLog(eventId: UUID) {
    try {
        eventLogJpaRepository.deleteById(eventId)
    } catch (ex: Exception) {
        logger.info("[eventId: $eventId] EventLog 롤백 실패")
    }
}