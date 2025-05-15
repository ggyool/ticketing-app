package com.ggyool.common.event

import java.time.LocalDateTime

interface DomainEvent<ID, P> {
    val eventId: String
    val version: Long?
    val aggregateId: ID
    val aggregateType: String
    val eventType: String
    val timeStamp: LocalDateTime
    val payload: P
}