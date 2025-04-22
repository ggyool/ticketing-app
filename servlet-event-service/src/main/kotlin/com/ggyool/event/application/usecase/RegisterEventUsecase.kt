package com.ggyool.event.application.usecase

interface RegisterEventUsecase {

    fun registerEvent(registerEventInput: RegisterEventInput): Long

    data class RegisterEventInput(
        val name: String,
        val ticketQuantity: Long,
    )
}