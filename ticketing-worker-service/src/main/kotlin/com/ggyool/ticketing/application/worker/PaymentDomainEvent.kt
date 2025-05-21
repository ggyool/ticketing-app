package com.ggyool.ticketing.application.worker

import com.ggyool.common.event.DomainEvent
import java.time.LocalDateTime
import java.util.*

data class PaymentDomainEvent(
    override val eventId: String,
    override val version: Long,
    override val aggregateId: UUID,
    override val aggregateType: String,
    override val eventType: String,
    override val timeStamp: LocalDateTime,
    override val payload: Payload,
) : DomainEvent<UUID, PaymentDomainEvent.Payload> {

    enum class PaymentStatus {
        CREATED,
        SUCCEEDED,
        FAILED,
        CANCELLED,
        DELETED, ;

        fun succeed(): Boolean = this == SUCCEEDED
    }

    data class Payload(
        val id: UUID,
        val eventId: Long,
        val userId: Long,
        val ticketId: UUID,
        val pgPaymentId: String?,
        val status: PaymentStatus,
        val totalAmount: Long,
        val pointAmount: Long,
        val finalAmount: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}