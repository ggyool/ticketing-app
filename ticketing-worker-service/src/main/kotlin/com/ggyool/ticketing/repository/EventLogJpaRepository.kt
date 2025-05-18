package com.ggyool.ticketing.repository

import com.ggyool.ticketing.repository.entity.EventLogEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface EventLogJpaRepository : CrudRepository<EventLogEntity, UUID> {

    @Query(
        nativeQuery = true,
        value = """
        SELECT * FROM event_log 
        WHERE aggregate_type = :aggregateType 
          AND aggregate_id = :aggregateId 
          AND version = :version
    """
    )
    fun findDomainEventLog(
        aggregateType: String,
        aggregateId: String,
        version: Long
    ): EventLogEntity?
}