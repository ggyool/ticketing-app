package com.ggyool.event.application.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.event.DeadLetterEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class DeadLetterKafkaProducer(
    @Qualifier("deadLetterKafkaTemplate")
    private val deadLetterKafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun send(event: DeadLetterEvent) {
        try {
            deadLetterKafkaTemplate.send(
                DeadLetterEvent.COMMON_DLT_TOPIC,
                event.eventId.toString(),
                objectMapper.writeValueAsString(event)
            )
            logger.info("[{}] Dead Letter 이벤트 발급 성공 ({})", event.eventId, event)
        } catch (ex: Exception) {
            logger.error(
                "[{}] Dead Letter 이벤트 발급 실패 (event: {} / ex: {})",
                event.eventId,
                event,
                ex.stackTraceToString()
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}