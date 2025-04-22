package com.ggyool.event.application.usecase.service

import com.ggyool.event.application.usecase.ModifyEventUsecase
import com.ggyool.event.exception.EventAppException
import com.ggyool.event.repository.EventJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ModifyEventService(
    private val eventRepository: EventJpaRepository
) : ModifyEventUsecase {

    @Transactional
    override fun modifyEvent(id: Long, modifyEventInput: ModifyEventUsecase.ModifyEventInput) {
        val entity = eventRepository.findByIdOrNull(id) ?: throw EventAppException(
            HttpStatus.NOT_FOUND,
            "id가 ${id}인 event가 존재하지 않습니다"
        )

        entity.name = modifyEventInput.name
        entity.ticketQuantity = modifyEventInput.ticketQuantity
    }
}