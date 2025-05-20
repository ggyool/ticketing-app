package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.payment.application.usecase.RegisterPaymentUsecase
import com.ggyool.payment.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import produceWithDlt
import java.util.*

@Component
class PaymentCreateRequestConsumer(
    private val registerPaymentUsecase: RegisterPaymentUsecase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["payment.create.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-create-request-group",
        concurrency = "2"
    )
    fun listenPaymentCreateRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue(record.value(), PaymentCreateRequest::class.java)
        val eventId = request.eventId
        val userId = request.userId
        val ticketId = request.ticketId
        val paymentId = registerPaymentUsecase.registerPayment(
            RegisterPaymentUsecase.RegisterPaymentInput(
                eventId = eventId,
                userId = userId,
                ticketId = ticketId,
            )
        )
        val response = PaymentCreateResponse(
            sagaId = request.sagaId,
            succeed = true,
            userId = request.userId,
            eventId = request.eventId,
            paymentId = paymentId
        )
        produceWithDlt(
            "payment.create.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class PaymentCreateRequest(
        val sagaId: String?,
        val ticketId: String,
        val eventId: Long,
        val userId: Long,
    )

    data class PaymentCreateResponse(
        val sagaId: String?,
        val succeed: Boolean,
        val userId: Long,
        val eventId: Long,
        val paymentId: UUID,
    )
}