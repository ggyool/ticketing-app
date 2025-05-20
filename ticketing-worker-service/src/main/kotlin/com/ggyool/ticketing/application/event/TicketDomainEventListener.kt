package com.ggyool.ticketing.application.event

import com.ggyool.ticketing.helper.produceWithDlt
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TicketDomainEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenTicketDomainEvent(ticketDomainEvent: TicketDomainEvent) {
        produceWithDlt("domain.ticket", ticketDomainEvent.eventId, ticketDomainEvent)
    }
}