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
class CheckActiveUserRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userCheckService: UserCheckService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["user.check.active.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "check-active-user-request-group",
        concurrency = "2"
    )
    fun listenCheckActiveUserRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue<CheckActiveUserRequest>(record.value())
        val response = CheckActiveUserResponse(
            request.sagaId,
            request.userId,
            userCheckService.isActiveUser(request.userId)
        )
        // TODO change helper
        kafkaTemplate.send(
            "user.check.active.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class CheckActiveUserRequest(
        val sagaId: String?,
        val userId: Long
    )

    data class CheckActiveUserResponse(
        val sagaId: String?,
        val userId: Long,
        val isActive: Boolean,
    )
}