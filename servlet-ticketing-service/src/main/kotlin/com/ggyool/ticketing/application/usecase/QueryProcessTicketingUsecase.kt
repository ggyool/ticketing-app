package com.ggyool.ticketing.application.usecase

import com.ggyool.ticketing.repository.entity.SagaContextEntity

interface QueryProcessTicketingUsecase {

    fun queryProcessTicketing(ticketId: String): SagaContextEntity
}