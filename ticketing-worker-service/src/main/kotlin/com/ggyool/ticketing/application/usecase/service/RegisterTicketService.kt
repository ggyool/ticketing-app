package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.RegisterTicketUsecase
import com.ggyool.ticketing.exception.TicketingWorkerException
import com.ggyool.ticketing.repository.TicketJpaRepository
import com.ggyool.ticketing.repository.entity.TicketEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RegisterTicketService(
    private val ticketRepository: TicketJpaRepository,
) : RegisterTicketUsecase {

    @Transactional
    override fun registerTicket(registerTicketInput: RegisterTicketUsecase.RegisterTicketInput) {
        val ticketId = registerTicketInput.ticketId
        validateDuplicatedTicket(ticketId)

        ticketRepository.save(
            TicketEntity(
                id = ticketId,
                eventId = registerTicketInput.eventId,
                userId = registerTicketInput.userId,
                status = registerTicketInput.status

            )
        )
    }

    private fun validateDuplicatedTicket(ticketId: UUID) {
        if (ticketRepository.findByIdOrNull(ticketId) != null) {
            throw TicketingWorkerException("[ticketId: $ticketId] 이미 생성된 티켓입니다")
        }
    }
}