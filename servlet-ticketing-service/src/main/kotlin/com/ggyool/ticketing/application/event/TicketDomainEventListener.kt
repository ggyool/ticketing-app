package com.ggyool.ticketing.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TicketDomainEventListener(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    @TransactionalEventListener
    fun listenTicketCreateEvent(ticketCreateEvent: TicketCreateEvent) {
        kafkaTemplate.send(
            "ticket.create",
            ticketCreateEvent.payload.id.toString(),
            objectMapper.writeValueAsString(ticketCreateEvent),
        )
    }
}