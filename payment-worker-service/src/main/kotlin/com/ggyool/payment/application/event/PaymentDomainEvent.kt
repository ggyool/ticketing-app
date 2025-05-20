package com.ggyool.payment.application.event

import com.ggyool.common.event.DomainEvent
import com.ggyool.payment.repository.entity.PaymentEntity
import com.ggyool.payment.repository.entity.PaymentStatus
import java.time.LocalDateTime
import java.util.*

class PaymentDomainEvent(
    override val eventId: String,
    override val version: Long?,
    override val aggregateId: UUID,
    override val aggregateType: String,
    override val eventType: String,
    override val timeStamp: LocalDateTime,
    override val payload: Payload
) : DomainEvent<UUID, PaymentDomainEvent.Payload> {

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
    ) {

        companion object {
            fun from(entity: PaymentEntity): Payload {
                return Payload(
                    id = entity.id,
                    eventId = entity.eventId,
                    userId = entity.userId,
                    ticketId = entity.ticketId,
                    pgPaymentId = entity.pgPaymentId,
                    status = entity.status,
                    totalAmount = entity.totalAmount,
                    pointAmount = entity.pointAmount,
                    finalAmount = entity.finalAmount,
                    createdAt = entity.createdAt!!,
                    updatedAt = entity.updatedAt!!,
                )
            }
        }
    }
}