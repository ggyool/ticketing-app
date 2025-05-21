package com.ggyool.ticketing.repository

import com.ggyool.ticketing.repository.entity.TicketEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TicketJpaRepository : JpaRepository<TicketEntity, UUID>