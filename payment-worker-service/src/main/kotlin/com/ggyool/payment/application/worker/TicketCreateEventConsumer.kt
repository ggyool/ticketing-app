package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.payment.application.usecase.RegisterPaymentUsecase
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TicketCreateEventConsumer(
    private val registerPaymentUsecase: RegisterPaymentUsecase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [CREATE_TICKET_TOPIC],
        containerFactory = "ticketKafkaListenerContainerFactory",
        groupId = "create-ticket-group",
        concurrency = "2"
    )
    fun listenCreateTicket(record: ConsumerRecord<String, String>) {
        val ticketId = record.key()
        val event = objectMapper.readValue<TicketCreateEvent>(record.value())
        // TODO: CDL 도입해야함
        registerPaymentUsecase.registerPayment(
            event.payload.eventId,
            event.payload.userId,
            event.payload.id
        )
    }


    companion object {
        private const val CREATE_TICKET_TOPIC: String = "ticket.create"
    }
}