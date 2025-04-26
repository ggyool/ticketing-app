package com.ggyool.payment.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentDomainEventListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenPaymentCreateEvent(paymentCreateEvent: PaymentCreateEvent) {
        kafkaTemplate.send(
            "payment.create",
            paymentCreateEvent.payload.id.toString(),
            objectMapper.writeValueAsString(paymentCreateEvent),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenPaymentUpdateEvent(paymentUpdateEvent: PaymentCreateEvent) {
        kafkaTemplate.send(
            "payment.update",
            paymentUpdateEvent.payload.id.toString(),
            objectMapper.writeValueAsString(paymentUpdateEvent),
        )
    }
}