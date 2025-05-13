package com.ggyool.ticketing.application.usecase

interface ReserveTicketingUsecase {

    fun reserveTicketing(reserveTicketingInput: ReserveTicketingInput)

    data class ReserveTicketingInput(
        val eventId: Long,
        val userId: Long,
    )
}