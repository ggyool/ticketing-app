package com.ggyool.event.application.usercase

import java.time.LocalDateTime

interface ReadEventUsecase {

    fun readEvent(id: Long): ReadEventOutput

    data class ReadEventOutput(
        val id: Long,
        val name: String,
        val ticketQuantity: Long,
        val version: Long,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    )
}