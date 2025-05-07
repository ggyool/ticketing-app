package com.ggyool.ticketing.application.service

import com.ggyool.common.event.DomainEvent
import com.ggyool.ticketing.repository.EventLogJpaRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class EventCheckService(
    private val eventLogJpaRepository: EventLogJpaRepository
) {

    fun isDuplicated(eventId: String): Boolean {
        return eventLogJpaRepository.existsById(UUID.fromString(eventId))
    }

    // 확실한 순서 체크를 위해 버전 체크 도입
    // ex. create와 update가 거의 동시에 발생해서 update 컨슈밍을 먼저 함
    fun <T : DomainEvent<*, *>> isValidOrder(event: T): Boolean {
        val version = event.version
            ?: throw IllegalArgumentException("[${event.eventId}] version이 존재하는 경우에만 호출해야합니다. $event")
        if (version == 0L) {
            return true
        }
        val previousVersionEventLog = eventLogJpaRepository.findEventLog(
            event.aggregateType,
            event.aggregateId.toString(),
            version - 1
        )
        return previousVersionEventLog != null
    }

}