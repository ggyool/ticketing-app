package com.ggyool.ticketing.repository

import com.ggyool.ticketing.repository.entity.TicketEntity
import org.springframework.data.repository.CrudRepository

interface TicketJpaRepository : CrudRepository<TicketEntity, Long>