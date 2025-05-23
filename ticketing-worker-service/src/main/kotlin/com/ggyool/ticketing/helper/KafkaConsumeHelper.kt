package com.ggyool.ticketing.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.event.DeadLetterEvent
import com.ggyool.ticketing.application.producer.DeadLetterKafkaProducer
import com.ggyool.ticketing.application.usecase.service.EventLogService
import com.ggyool.ticketing.application.worker.EventDomainEvent
import com.ggyool.ticketing.repository.entity.EventLogEntity
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import java.util.*

private val deadLetterKafkaProducer: DeadLetterKafkaProducer by lazy {
    ApplicationContextProvider.getBean(DeadLetterKafkaProducer::class.java)
}

private val eventLogService: EventLogService by lazy {
    ApplicationContextProvider.getBean(EventLogService::class.java)
}

private val objectMapper: ObjectMapper by lazy {
    ApplicationContextProvider.getBean(ObjectMapper::class.java)
}

private val logger: Logger = LoggerFactory.getLogger("KafkaConsumeHelper")

fun consumeWithDlt(
    record: ConsumerRecord<String, String>,
    acknowledgment: Acknowledgment,
    block: () -> Unit,
) {
    val eventId = UUID.fromString(record.key())
    try {
        if (eventLogService.isDuplicated(eventId)) {
            logger.info("[eventId: $eventId] 중복 메시지 입니다. ($record)")
            return
        }
        eventLogService.save(EventLogEntity(eventId = eventId))
        block()
    } catch (ex: Exception) {
        logger.error("[${record.key()}] Consuming Error $record", ex)
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

fun consumeDomainEventWithDlt(
    record: ConsumerRecord<String, String>,
    acknowledgment: Acknowledgment,
    block: () -> Unit,
) {
    val event = objectMapper.readValue(record.value(), EventDomainEvent::class.java)
    val eventId = UUID.fromString(event.eventId)
    try {
        if (eventLogService.isDuplicated(eventId)) {
            logger.info("[eventId: $eventId] 중복 메시지 입니다. ($record)")
            return
        }
        eventLogService.checkValidOrder(event)
        eventLogService.save(event.toEventLogEntity())
        block()
    } catch (ex: Exception) {
        logger.error("[${record.key()}] Consuming Error $record", ex)
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
        eventLogService.deleteById(eventId)
    } catch (ex: Exception) {
        logger.info("[eventId: $eventId] EventLog 롤백 실패")
    }
}