package com.ggyool.event.application.usercase

interface RegisterEventUsecase {

    fun registerEvent(registerEventInput: RegisterEventInput): Long

    data class RegisterEventInput(
        val name: String,
        val ticketQuantity: Long,
    )
}