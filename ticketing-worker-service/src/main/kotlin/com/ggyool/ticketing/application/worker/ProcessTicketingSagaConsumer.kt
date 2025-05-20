package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.common.saga.model.SagaResponse
import com.ggyool.ticketing.application.saga.ProcessTicketingSaga
import com.ggyool.ticketing.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProcessTicketingSagaConsumer(
    private val objectMapper: ObjectMapper,
    private val processTicketingSaga: ProcessTicketingSaga,
) {

    @KafkaListener(
        topics = ["ticketing.process"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "process-ticketing-saga-event-group",
        concurrency = "2"
    )
    fun listenProcessTicketingSagaTrigger(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val input = objectMapper.readValue<ProcessTicketingInput>(record.value())
        val payload = ProcessTicketingSaga.Payload(
            sagaId = UUID.randomUUID(),
            referenceId = UUID.fromString(input.ticketId),
            eventId = input.eventId,
            userId = input.userId,
            ticketId = UUID.fromString(input.ticketId),
            totalAmount = input.totalAmount,
            point = input.point,
        )
        processTicketingSaga.start("ticketing.process", payload)
    }

    @KafkaListener(
        topics = [
            "user.check.active.response",
            "user.reserve.point.response",
            "user.restore.point.response",
            "user.check.fraud.response",
            "payment.create.response",
            "payment.delete.response",
        ],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "process-ticketing-saga-event-group",
        concurrency = "2"
    )
    fun listenProcessTicketingSagaResponse(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val sagaId = UUID.fromString(
            objectMapper.readTree(record.value()).get("sagaId").asText()
        )
        processTicketingSaga.onResponse(SagaResponse(sagaId, record.value()))
    }


    data class ProcessTicketingInput(
        val eventId: Long,
        val userId: Long,
        val ticketId: String,
        val totalAmount: Long,
        val point: Long,
    )
}