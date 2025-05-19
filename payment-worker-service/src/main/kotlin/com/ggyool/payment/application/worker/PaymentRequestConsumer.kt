package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.payment.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import produceWithDlt
import java.util.*

@Component
class PaymentRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["payment.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-request-group",
        concurrency = "2"
    )
    fun listenPaymentRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue(record.value(), PaymentRequest::class.java)
        // TODO add logic
        val response = PaymentResponse(
            sagaId = request.sagaId,
            succeed = true,
            userId = request.userId,
            eventId = request.eventId,
            paymentId = UUID.randomUUID()
        )
        produceWithDlt(
            "payment.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class PaymentRequest(
        val sagaId: String?,
        val eventId: Long,
        val userId: Long,
        val paymentInfo: String,
    )

    data class PaymentResponse(
        val sagaId: String?,
        val succeed: Boolean,
        val userId: Long,
        val eventId: Long,
        val paymentId: UUID,
    )
}