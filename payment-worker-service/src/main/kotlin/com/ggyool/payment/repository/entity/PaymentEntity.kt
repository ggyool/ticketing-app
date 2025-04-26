package com.ggyool.payment.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class, PaymentEntityListener::class)
@Table(name = "payment")
@Entity
class PaymentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    var eventId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var ticketId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @Version
    var version: Long?,

    @CreatedDate
    var createdAt: LocalDateTime?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) {
    constructor(eventId: Long, userId: Long, ticketId: Long) : this(
        id = null,
        eventId = eventId,
        userId = userId,
        ticketId = ticketId,
        status = PaymentStatus.CREATED,
        version = null,
        createdAt = null,
        updatedAt = null,
    )
}