package com.ggyool.ticketing.application.usecase

interface ProcessTicketingUsecase {

    fun processTicketing(processTicketingInput: ProcessTicketingInput)

    data class ProcessTicketingInput(
        val eventId: Long,
        val userId: Long,
        val point: Long,
        val paymentInfo: String,
    )
}