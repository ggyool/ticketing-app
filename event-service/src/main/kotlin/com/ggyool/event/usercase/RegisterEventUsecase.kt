package com.ggyool.event.usercase

interface RegisterEventUsecase {

    fun registerEvent(registerEventInput: RegisterEventInput): Long

    data class RegisterEventInput(
        val name: String,
        val ticketQuantity: Long,
    )
}