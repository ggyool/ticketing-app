package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.RegisterTicketUsecase
import com.ggyool.ticketing.repository.TicketJpaRepository
import com.ggyool.ticketing.repository.entity.TicketEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

// TODO: 결제까지 완료된 티켓 생성
@Service
class RegisterTicketService(
    private val ticketJpaRepository: TicketJpaRepository
) : RegisterTicketUsecase {

    @Transactional
    override fun registerTicket(eventId: Long, userId: Long, reservedAt: LocalDateTime): Long {
        val entity = ticketJpaRepository.save(
            TicketEntity(
                eventId = eventId,
                userId = userId,
                reservedAt = reservedAt
            )
        )
        return entity.id!!
    }
}