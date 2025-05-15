package com.ggyool.user.repository

import com.ggyool.user.repository.entity.EventLogEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface EventLogJpaRepository : CrudRepository<EventLogEntity, UUID>