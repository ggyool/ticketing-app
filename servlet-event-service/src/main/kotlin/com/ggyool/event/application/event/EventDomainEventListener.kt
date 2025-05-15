package com.ggyool.event.application.event

import com.ggyool.event.helper.produceWithDlt
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EventDomainEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenEventDomainEvent(eventDomainEvent: EventDomainEvent) {
        produceWithDlt("domain.event", eventDomainEvent.eventId, eventDomainEvent)
    }
}
