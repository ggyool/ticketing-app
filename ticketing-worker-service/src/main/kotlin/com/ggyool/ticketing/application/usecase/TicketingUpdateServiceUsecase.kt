package com.ggyool.ticketing.application.usecase

import com.ggyool.ticketing.application.worker.EventDomainEvent

interface TicketingUpdateServiceUsecase {

    fun updateTicket(event: EventDomainEvent)
}
