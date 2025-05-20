package com.ggyool.payment.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Table(name = "payment")
@Entity
class PaymentEntity(
    @Id
    val id: UUID,
    val eventId: Long,
    val userId: Long,
    val ticketId: UUID,
    val pgPaymentId: String,
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,
    val totalAmount: Long,
    val pointAmount: Long,
    val finalAmount: Long,
    val version: Long,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)