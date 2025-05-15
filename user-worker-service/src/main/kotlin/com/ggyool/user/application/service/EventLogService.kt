package com.ggyool.user.application.service

import com.ggyool.user.repository.EventLogJpaRepository
import com.ggyool.user.repository.entity.EventLogEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class EventLogService(
    private val eventLogJpaRepository: EventLogJpaRepository
) {

    @Transactional
    fun save(eventId: UUID) {
        eventLogJpaRepository.save(EventLogEntity(eventId = eventId))
    }

    fun isDuplicated(eventId: UUID): Boolean {
        return eventLogJpaRepository.existsById(eventId)
    }
}