package com.ggyool.payment.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "payment_history",
    indexes = [
        Index(name = "idx_payment_history_payment_id", columnList = "payment_id")
    ]
)
@Entity
class PaymentHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false)
    val paymentId: UUID,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,

    @CreatedDate
    var createdAt: LocalDateTime?,
) {
    constructor(
        paymentId: UUID,
        status: PaymentStatus
    ) : this(
        id = null,
        paymentId = paymentId,
        status = status,
        createdAt = null,
    )
}