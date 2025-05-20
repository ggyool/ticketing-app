package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.TicketingUpdateServiceUsecase
import com.ggyool.ticketing.application.worker.EventDomainEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketingUpdateService(
    private val stringRedisTemplate: StringRedisTemplate,
) : TicketingUpdateServiceUsecase {

    @Transactional
    override fun updateTicket(event: EventDomainEvent) {
        stringRedisTemplate.opsForValue().set(
            TICKETING_QUANTITY_KEY.format(event.aggregateId),
            event.payload.ticketQuantity.toString()
        )
    }

    companion object {
        private const val TICKETING_QUANTITY_KEY: String = "ticketing:quantity:%s"
    }
}