package com.ggyool.common.event

import java.time.LocalDateTime
import java.util.*

data class DeadLetterEvent(
    val eventId: UUID = UUID.randomUUID(),
    val retryCount: Int = 0,
    val maxRetryCount: Int = 3,
    val retryIntervalMillis: Int = 10000,
    val timeStamp: LocalDateTime = LocalDateTime.now(),
    val exceptionName: String,
    val reissuedTopic: String,
    val reissuedKey: String,
    val reissuedPayload: String,
) {
    companion object {
        const val COMMON_DLT_TOPIC = "common.dlt"
    }
}