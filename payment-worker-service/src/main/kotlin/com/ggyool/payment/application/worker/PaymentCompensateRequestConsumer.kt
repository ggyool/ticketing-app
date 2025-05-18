package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.payment.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentCompensateRequestConsumer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["payment.compensate.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-compensate-request-group",
        concurrency = "2"
    )
    fun listenPaymentCompensateRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue(record.value(), PaymentCompensateRequest::class.java)
        // TODO add logic
        val response = PaymentCompensateResponse(
            sagaId = request.sagaId,
            paymentId = UUID.randomUUID()
        )
        // TODO change helper
        kafkaTemplate.send(
            "payment.compensate.response",
            UUID.randomUUID().toString(),
            objectMapper.writeValueAsString(response)
        )
    }

    data class PaymentCompensateRequest(
        val sagaId: String?,
        val paymentId: UUID,
    )

    data class PaymentCompensateResponse(
        val sagaId: String?,
        val paymentId: UUID,
    )
}