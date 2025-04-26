package com.ggyool.payment.repository.entity

import com.ggyool.payment.application.event.PaymentCreateEvent
import com.ggyool.payment.application.event.PaymentUpdateEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class PaymentEntityListener(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @PostPersist
    fun postPersist(paymentEntity: PaymentEntity) {
        applicationEventPublisher.publishEvent(
            PaymentCreateEvent(
                payload = PaymentCreateEvent.Payload(
                    id = paymentEntity.id!!,
                    eventId = paymentEntity.eventId,
                    userId = paymentEntity.userId,
                    status = paymentEntity.status.toString(),
                    version = paymentEntity.version!!,
                    createdAt = paymentEntity.createdAt!!,
                    updatedAt = paymentEntity.updatedAt!!
                )
            )
        )
    }

    @PostUpdate
    fun postUpdate(paymentEntity: PaymentEntity) {
        applicationEventPublisher.publishEvent(
            PaymentUpdateEvent(
                payload = PaymentUpdateEvent.Payload(
                    id = paymentEntity.id!!,
                    eventId = paymentEntity.eventId,
                    userId = paymentEntity.userId,
                    status = paymentEntity.status.toString(),
                    version = paymentEntity.version!!,
                    createdAt = paymentEntity.createdAt!!,
                    updatedAt = paymentEntity.updatedAt!!
                )
            )
        )
    }
}