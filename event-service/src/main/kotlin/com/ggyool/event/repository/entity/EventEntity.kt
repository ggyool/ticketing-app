package com.ggyool.event.repository.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Table(name = "event")
@Entity
class EventEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(nullable = false)
    var name: String,

    var ticketQuantity: Long,

    @Version
    var version: Long?,

    @CreatedDate
    var createdAt: LocalDateTime?,

    @LastModifiedDate
    var updatedAt: LocalDateTime?,
) {
    constructor(name: String, ticketQuantity: Long) : this(
        id = null,
        name = name,
        ticketQuantity = ticketQuantity,
        version = null,
        createdAt = null,
        updatedAt = null,
    )

    constructor(id: Long, name: String, ticketQuantity: Long) : this(
        id = id,
        name = name,
        ticketQuantity = ticketQuantity,
        version = null,
        createdAt = null,
        updatedAt = null,
    )
}