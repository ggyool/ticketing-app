package com.ggyool.ticketing.repository.entity

import com.ggyool.common.event.ConsumedEvent
import jakarta.annotation.Nullable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "event_log",
    indexes = [
        Index(name = "idx_find_event_log", columnList = "aggregate_type, aggregate_id, version")
    ]
)
@Entity
data class EventLogEntity(
    @Id
    override val eventId: UUID,
    @Nullable
    val aggregateType: String? = null,
    @Nullable
    val aggregateId: String? = null,
    @Nullable
    val version: Long? = null,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
) : ConsumedEvent<UUID>


