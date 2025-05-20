package com.ggyool.payment.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class, PaymentEntityListener::class)
@Table(
    name = "payment",
    indexes = [
        Index(name = "idx_payment_ticket_id", columnList = "ticket_id")
    ]
)
@Entity
class PaymentEntity(

    @Id
    val id: UUID,

    @Column(nullable = false)
    val eventId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val ticketId: UUID,

    @Column(nullable = true)
    val pgPaymentId: UUID?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,

    val totalAmount: Long,

    val pointAmount: Long,

    val finalAmount: Long,

    @Version
    var version: Long?,

    @CreatedDate
    var createdAt: LocalDateTime?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) {
    constructor(
        eventId: Long,
        userId: Long,
        ticketId: UUID,
        totalAmount: Long,
        pointAmount: Long
    ) : this(
        id = UUID.randomUUID(),
        eventId = eventId,
        userId = userId,
        ticketId = ticketId,
        pgPaymentId = null,
        status = PaymentStatus.CREATED,
        totalAmount = totalAmount,
        pointAmount = pointAmount,
        finalAmount = totalAmount - pointAmount,
        version = null,
        createdAt = null,
        updatedAt = null,
    )
}