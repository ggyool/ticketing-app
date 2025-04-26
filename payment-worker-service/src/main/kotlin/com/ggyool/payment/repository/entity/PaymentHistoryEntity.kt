package com.ggyool.payment.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Table(name = "payment_history")
@Entity
class PaymentHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    var paymentId: Long,

    @Column(nullable = false)
    var eventId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var ticketId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @CreatedDate
    var createdAt: LocalDateTime?,
) {
    constructor(
        paymentId: Long,
        eventId: Long,
        userId: Long,
        ticketId: Long,
        status: PaymentStatus
    ) : this(
        id = null,
        paymentId = paymentId,
        eventId = eventId,
        userId = userId,
        ticketId = ticketId,
        status = status,
        createdAt = null,
    )
}