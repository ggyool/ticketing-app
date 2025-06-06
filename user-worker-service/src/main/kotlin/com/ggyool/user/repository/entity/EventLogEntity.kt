package com.ggyool.user.repository.entity

import com.ggyool.common.event.ConsumedEvent
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "event_log")
@Entity
data class EventLogEntity(
    @Id
    override val eventId: UUID,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ConsumedEvent<UUID>