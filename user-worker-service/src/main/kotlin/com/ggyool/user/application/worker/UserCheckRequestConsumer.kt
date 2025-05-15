package com.ggyool.user.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.user.application.helper.consumeWithDlt
import com.ggyool.user.application.producer.DeadLetterKafkaProducer
import com.ggyool.user.application.producer.KafkaProducer
import com.ggyool.user.application.service.EventLogService
import com.ggyool.user.application.service.UserCheckService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserCheckRequestConsumer(
    private val kafkaProducer: KafkaProducer,
    private val deadLetterKafkaProducer: DeadLetterKafkaProducer,
    private val userCheckService: UserCheckService,
    private val eventLogService: EventLogService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["user.check.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "user-check-request-group",
        concurrency = "2"
    )
    fun listenUserCheckRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge, deadLetterKafkaProducer) {
        val request = objectMapper.readValue<UserCheckRequest>(record.value())
        val eventId = record.key()

        if (eventLogService.isDuplicated(UUID.fromString(eventId))) {
            logger.info("[eventId: $eventId] 중복 메시지 입니다. ($record)")
            return
        }
        val userId = request.userId
        val userCheckResponse = UserCheckResponse(userId, userCheckService.isActiveUser(userId))
        kafkaProducer.send(
            "user.check.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(userCheckResponse)
        )
    }

    data class UserCheckRequest(
        val userId: Long
    )

    data class UserCheckResponse(
        val userId: Long,
        val isActive: Boolean,
    )

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}