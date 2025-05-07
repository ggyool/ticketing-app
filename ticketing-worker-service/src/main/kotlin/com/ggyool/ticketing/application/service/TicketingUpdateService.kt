package com.ggyool.ticketing.application.service

import com.ggyool.ticketing.application.worker.EventDomainEvent
import com.ggyool.ticketing.repository.EventLogJpaRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Service
class TicketingUpdateService(
    private val stringRedisTemplate: StringRedisTemplate,
    private val eventLogJpaRepository: EventLogJpaRepository,
) {

    @Transactional
    fun updateTicket(event: EventDomainEvent): Boolean {
        try {
            eventLogJpaRepository.save(event.toEventLogEntity())
            stringRedisTemplate.opsForValue().set(
                TICKETING_QUANTITY_KEY.format(event.aggregateId),
                event.payload.ticketQuantity.toString()
            )
        } catch (ex: Exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            return false
        }
        return true
    }

    companion object {
        private const val TICKETING_QUANTITY_KEY: String = "ticketing:quantity:%s"
    }
}