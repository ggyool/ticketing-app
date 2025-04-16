package com.ggyool.event.repository

import com.ggyool.event.repository.entity.EventEntity
import org.springframework.data.repository.CrudRepository

interface EventJpaRepository : CrudRepository<EventEntity, Long>