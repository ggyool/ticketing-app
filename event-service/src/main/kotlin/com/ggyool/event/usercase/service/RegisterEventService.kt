package com.ggyool.event.usercase.service

import com.ggyool.event.repository.EventJpaRepository
import com.ggyool.event.repository.entity.EventEntity
import com.ggyool.event.usercase.RegisterEventUsecase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterEventService(
    private val eventRepository: EventJpaRepository
) : RegisterEventUsecase {

    @Transactional
    override fun registerEvent(registerEventInput: RegisterEventUsecase.RegisterEventInput): Long {
        val entity = eventRepository.save(
            EventEntity(
                registerEventInput.name,
                registerEventInput.ticketQuantity,
            )
        )
        return entity.id!!
    }
}