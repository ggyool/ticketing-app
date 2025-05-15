package com.ggyool.user.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.user.application.service.UserCheckService
import com.ggyool.user.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserCheckRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userCheckService: UserCheckService,
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
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue<UserCheckRequest>(record.value())
        val userId = request.userId
        val userCheckResponse = UserCheckResponse(userId, userCheckService.isActiveUser(userId))
        kafkaTemplate.send(
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
}