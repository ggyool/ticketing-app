package com.ggyool.ticketing.application.usecase

interface ReserveTicketUsecase {

    fun reserveTicket(reserveTicketInput: ReserveTicketInput)

    data class ReserveTicketInput(
        val eventId: Long,
        val userId: Long,
    )
}