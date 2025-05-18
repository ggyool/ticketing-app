package com.ggyool.user.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.user.application.service.UserCheckService
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
class ReserveUserPointRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userCheckService: UserCheckService,
    private val userPointService: UserPointService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["user.reserve.point.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "reserve-user-point-group",
        concurrency = "2"
    )
    fun listenReserveUserPointRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue<ReserveUserPointRequest>(record.value())
        val userId = request.userId
        val point = request.point
        val response = if (userCheckService.isActiveUser(request.userId)) {
            ReserveUserPointResponse(
                sagaId = request.sagaId,
                userId = userId,
                point = point,
                reserved = userPointService.reservePoint(userId, point),
            )
        } else {
            ReserveUserPointResponse(
                sagaId = request.sagaId,
                userId = userId,
                point = point,
                reserved = false,
            )
        }
        // TODO change helper
        kafkaTemplate.send(
            "user.reserve.point.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class ReserveUserPointRequest(
        val sagaId: String?,
        val userId: Long,
        val point: Long
    )

    data class ReserveUserPointResponse(
        val sagaId: String?,
        val userId: Long,
        val point: Long,
        val reserved: Boolean,
    )
}