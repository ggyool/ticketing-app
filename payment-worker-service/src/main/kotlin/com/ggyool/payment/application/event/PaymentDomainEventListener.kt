package com.ggyool.payment.application.event

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import produceWithDlt

@Component
class PaymentDomainEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun listenPaymentDomainEvent(paymentDomainEvent: PaymentDomainEvent) {
        produceWithDlt("domain.payment", paymentDomainEvent.eventId, paymentDomainEvent)
    }
}