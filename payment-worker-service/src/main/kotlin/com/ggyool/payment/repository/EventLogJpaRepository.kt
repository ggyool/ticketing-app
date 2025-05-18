package com.ggyool.payment.repository

import com.ggyool.payment.repository.entity.EventLogEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface EventLogJpaRepository : CrudRepository<EventLogEntity, UUID>