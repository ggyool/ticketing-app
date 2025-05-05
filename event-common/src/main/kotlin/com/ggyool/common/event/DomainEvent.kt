package com.ggyool.common.event

import java.time.LocalDateTime

interface DomainEvent<ID, P> {
    var eventId: String?
    var version: Long?
    val aggregateId: ID
    val aggregateType: String
    val eventType: String
    val timeStamp: LocalDateTime
    val payload: P
}