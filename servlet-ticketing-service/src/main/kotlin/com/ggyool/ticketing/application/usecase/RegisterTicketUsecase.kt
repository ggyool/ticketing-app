package com.ggyool.ticketing.application.usecase

import java.time.LocalDateTime

interface RegisterTicketUsecase {
    fun registerTicket(
        eventId: Long,
        userId: Long,
        reservedAt: LocalDateTime
    ): Long
}