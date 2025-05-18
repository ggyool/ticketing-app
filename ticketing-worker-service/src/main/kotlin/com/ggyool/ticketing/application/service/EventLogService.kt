package com.ggyool.ticketing.application.service

import com.ggyool.common.event.DomainEvent
import com.ggyool.ticketing.exception.TicketingWorkerException
import com.ggyool.ticketing.repository.EventLogJpaRepository
import com.ggyool.ticketing.repository.entity.EventLogEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class EventLogService(
    private val eventLogJpaRepository: EventLogJpaRepository
) {

    @Transactional
    fun save(entity: EventLogEntity) {
        eventLogJpaRepository.save(entity)
    }

    @Transactional
    fun deleteById(eventId: UUID) {
        eventLogJpaRepository.deleteById(eventId)
    }

    fun isDuplicated(eventId: UUID): Boolean {
        return eventLogJpaRepository.existsById(eventId)
    }

    // 확실한 순서 체크를 위해 버전 체크 도입
    // ex. create와 update가 거의 동시에 발생해서 update 컨슈밍을 먼저 함
    fun <T : DomainEvent<*, *>> checkValidOrder(event: T) {
        val version = event.version
            ?: throw TicketingWorkerException("[${event.eventId}] version이 존재하는 경우에만 호출해야합니다. $event")
        if (version == 0L) {
            return
        }
        eventLogJpaRepository.findDomainEventLog(
            event.aggregateType,
            event.aggregateId.toString(),
            version - 1
        )
            ?: throw throw TicketingWorkerException("[${event.eventId}] 이전 버전 이벤트가 적용되지 않았습니다. $event")
    }
}