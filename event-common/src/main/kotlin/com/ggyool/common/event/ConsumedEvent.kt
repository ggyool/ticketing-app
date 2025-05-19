package com.ggyool.common.event

import java.time.LocalDateTime

interface ConsumedEvent<ID> {
    val eventId: ID
    val timestamp: LocalDateTime
}