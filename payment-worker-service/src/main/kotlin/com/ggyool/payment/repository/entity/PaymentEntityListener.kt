package com.ggyool.payment.repository.entity

import com.ggyool.payment.application.event.PaymentDomainEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class PaymentEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(paymentEntity: PaymentEntity) {
        applicationEventPublisher.publishEvent(
            PaymentDomainEvent(
                UUID.randomUUID().toString(),
                version = paymentEntity.version!!,
                aggregateId = paymentEntity.id,
                aggregateType = "payment",
                eventType = "create",
                timeStamp = LocalDateTime.now(),
                payload = PaymentDomainEvent.Payload.from(paymentEntity)
            )
        )
    }

    @PostUpdate
    fun postUpdate(paymentEntity: PaymentEntity) {
        applicationEventPublisher.publishEvent(
            PaymentDomainEvent(
                UUID.randomUUID().toString(),
                version = paymentEntity.version!!,
                aggregateId = paymentEntity.id,
                aggregateType = "payment",
                eventType = "update",
                timeStamp = LocalDateTime.now(),
                payload = PaymentDomainEvent.Payload.from(paymentEntity)
            )
        )
    }
}