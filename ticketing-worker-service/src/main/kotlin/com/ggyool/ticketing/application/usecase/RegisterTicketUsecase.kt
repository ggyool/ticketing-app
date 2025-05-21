package com.ggyool.ticketing.application.usecase

import com.ggyool.ticketing.repository.entity.TicketStatus
import java.util.*

interface RegisterTicketUsecase {

    fun registerTicket(registerTicketInput: RegisterTicketInput)

    data class RegisterTicketInput(
        val ticketId: UUID,
        val eventId: Long,
        val userId: Long,
        val status: TicketStatus,
    )
}