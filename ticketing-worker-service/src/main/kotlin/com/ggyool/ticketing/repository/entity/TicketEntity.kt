package com.ggyool.ticketing.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class, TicketEntityListener::class)
@Table(name = "ticket")
@Entity
class TicketEntity(

    @Id
    val id: UUID,

    @Column(nullable = false)
    val eventId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: TicketStatus,

    @Version
    var version: Long?,

    @CreatedDate
    var createdAt: LocalDateTime?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) {
    constructor(id: UUID, eventId: Long, userId: Long, status: TicketStatus) : this(
        id = id,
        eventId = eventId,
        userId = userId,
        status = status,
        version = null,
        createdAt = null,
        updatedAt = null,
    )
}