package com.ggyool.event.application.usecase.service

import com.ggyool.event.application.usecase.ReadEventUsecase
import com.ggyool.event.exception.EventAppException
import com.ggyool.event.repository.EventJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ReadEventService(
    private val eventRepository: EventJpaRepository
) : ReadEventUsecase {

    override fun readEvent(id: Long): ReadEventUsecase.ReadEventOutput {
        val entity = eventRepository.findByIdOrNull(id) ?: throw EventAppException(
            HttpStatus.NOT_FOUND,
            "id가 ${id}인 event가 존재하지 않습니다"
        )

        return ReadEventUsecase.ReadEventOutput(
            id = entity.id!!,
            name = entity.name,
            ticketQuantity = entity.ticketQuantity,
            version = entity.version!!,
            createdAt = entity.createdAt!!,
            updatedAt = entity.updatedAt!!,
        )
    }
}