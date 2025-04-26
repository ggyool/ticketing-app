package com.ggyool.ticketing.application.usecase

interface ReserveTicketingUsecase {

    fun reserveTicketing(reserveTicketInput: ReserveTicketInput)

    data class ReserveTicketInput(
        val eventId: Long,
        val userId: Long,
    )
}