package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.ModifyTicketQuantityUsecase
import com.ggyool.ticketing.application.worker.EventDomainEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ModifyTicketQuantityService(
    private val stringRedisTemplate: StringRedisTemplate,
) : ModifyTicketQuantityUsecase {

    override fun modifyTicketQuantity(modifyTicketQuantityInput: ModifyTicketQuantityUsecase.ModifyTicketQuantityInput) {
        val eventId = modifyTicketQuantityInput.eventId
        val ticketQuantity = modifyTicketQuantityInput.ticketQuantity.toString()
        stringRedisTemplate.opsForValue().set(
            TICKETING_QUANTITY_KEY.format(eventId),
            ticketQuantity,
        )
    }

    companion object {
        private const val TICKETING_QUANTITY_KEY: String = "ticketing:quantity:%s"
    }
}