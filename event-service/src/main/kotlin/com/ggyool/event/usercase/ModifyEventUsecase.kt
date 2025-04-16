package com.ggyool.event.usercase

interface ModifyEventUsecase {

    fun modifyEvent(id: Long, modifyEventInput: ModifyEventInput)

    data class ModifyEventInput(
        val name: String,
        val ticketQuantity: Long
    )
}