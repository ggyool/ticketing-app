package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.ticketing.application.usecase.ModifyTicketQuantityUsecase
import com.ggyool.ticketing.application.usecase.service.ModifyTicketQuantityService
import com.ggyool.ticketing.helper.consumeDomainEventWithDlt
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class EventDomainEventConsumer(
    private val modifyTicketQuantityService: ModifyTicketQuantityService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [EVENT_DOMAIN_EVENT_TOPIC],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "event-domain-event-group",
        concurrency = "2"
    )
    fun listenEventDomainEvent(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeDomainEventWithDlt(record, acknowledge) {
        val event = objectMapper.readValue<EventDomainEvent>(record.value())
        val eventType = event.eventType

        if (eventType == "create" || eventType != "update") {
            modifyTicketQuantityService.modifyTicketQuantity(
                ModifyTicketQuantityUsecase.ModifyTicketQuantityInput(
                    eventId = event.aggregateId,
                    ticketQuantity = event.payload.ticketQuantity
                )
            )
        }
    }

    companion object {
        private const val EVENT_DOMAIN_EVENT_TOPIC: String = "domain.event"
    }
}