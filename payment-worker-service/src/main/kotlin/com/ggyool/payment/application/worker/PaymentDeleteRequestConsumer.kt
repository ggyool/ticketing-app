package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.payment.application.usecase.RemovePaymentUsecase
import com.ggyool.payment.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import produceWithDlt
import java.util.*

@Component
class PaymentDeleteRequestConsumer(
    private val removePaymentUsecase: RemovePaymentUsecase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["payment.delete.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-delete-request-group",
        concurrency = "2"
    )
    fun listenPaymentDeleteRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue(record.value(), PaymentDeleteRequest::class.java)
        removePaymentUsecase.removePayment(UUID.fromString(request.paymentId))
        val response = PaymentDeleteResponse(
            sagaId = request.sagaId,
            paymentId = request.paymentId
        )
        produceWithDlt(
            "payment.delete.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class PaymentDeleteRequest(
        val sagaId: String?,
        val paymentId: String,
    )

    data class PaymentDeleteResponse(
        val sagaId: String?,
        val paymentId: String,
    )
}