package com.ggyool.ticketing.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class, TicketEntityListener::class)
@Table(name = "ticket")
@Entity
class TicketEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    var eventId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var reservedAt: LocalDateTime,

    @Version
    var version: Long?,

    @CreatedDate
    var createdAt: LocalDateTime?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) {
    constructor(eventId: Long, userId: Long, reservedAt: LocalDateTime) : this(
        id = null,
        eventId = eventId,
        userId = userId,
        reservedAt = reservedAt,
        version = null,
        createdAt = null,
        updatedAt = null,
    )
}