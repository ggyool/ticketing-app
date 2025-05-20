package com.ggyool.payment.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.payment.application.usecase.FailPaymentUsecase
import com.ggyool.payment.application.usecase.SucceedPaymentUsecase
import com.ggyool.payment.helper.consumeWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentConfirmedRequestConsumer(
    private val succeedPaymentUsecase: SucceedPaymentUsecase,
    private val failPaymentUsecase: FailPaymentUsecase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = ["payment.confirmed.request"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-confirmed-request-group",
        concurrency = "2"
    )
    fun listenPaymentConfirmedRequest(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val request = objectMapper.readValue(record.value(), PaymentConfirmedRequest::class.java)
        val paymentId = UUID.fromString(request.paymentId)
        if (request.confirmed) {
            succeedPaymentUsecase.succeedPayment(paymentId, request.pgPaymentId)
        } else {
            failPaymentUsecase.failPayment(paymentId, request.pgPaymentId)
        }
    }

    data class PaymentConfirmedRequest(
        val paymentId: String,
        val pgPaymentId: String,
        val confirmed: Boolean,
    )
}