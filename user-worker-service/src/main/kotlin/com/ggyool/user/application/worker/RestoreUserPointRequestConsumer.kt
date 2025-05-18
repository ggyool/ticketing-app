package com.ggyool.user.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.user.application.service.UserPointService
import com.ggyool.user.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestoreUserPointRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userPointService: UserPointService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["user.restore.point.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "restore-user-point-group",
        concurrency = "2"
    )
    fun listenRestoreUserPointRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue<RestoreUserPointRequest>(record.value())
        val userId = request.userId
        val point = request.point
        userPointService.restorePoint(userId, point)
        val response = RestoreUserPointResponse(
            sagaId = request.sagaId,
            userId = userId,
            point = point,
            succeed = true
        )
        // TODO change helper
        kafkaTemplate.send(
            "user.restore.point.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class RestoreUserPointRequest(
        val sagaId: String?,
        val userId: Long,
        val point: Long
    )

    data class RestoreUserPointResponse(
        val sagaId: String?,
        val userId: Long,
        val point: Long,
        val succeed: Boolean,
    )
}