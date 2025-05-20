package com.ggyool.ticketing.application.usecase

interface ProcessTicketingUsecase {

    fun processTicketing(processTicketingInput: ProcessTicketingInput)

    data class ProcessTicketingInput(
        val ticketId: String,
        val eventId: Long,
        val userId: Long,
        val totalAmount: Long,
        val point: Long,
    )
}