package com.ggyool.ticketing.application.usecase

interface ModifyTicketQuantityUsecase {

    fun modifyTicketQuantity(modifyTicketQuantityInput: ModifyTicketQuantityInput)

    data class ModifyTicketQuantityInput(
        val eventId: Long,
        val ticketQuantity: Long,
    )
}
