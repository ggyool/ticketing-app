package com.ggyool.payment.application.usecase.service

import com.ggyool.payment.application.usecase.RegisterPaymentUsecase
import com.ggyool.payment.repository.PaymentHistoryJpaRepository
import com.ggyool.payment.repository.PaymentJpaRepository
import com.ggyool.payment.repository.entity.PaymentEntity
import com.ggyool.payment.repository.entity.PaymentHistoryEntity
import com.ggyool.payment.repository.entity.PaymentStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterPaymentService(
    private val paymentJpaRepository: PaymentJpaRepository,
    private val paymentHistoryJpaRepository: PaymentHistoryJpaRepository,
) : RegisterPaymentUsecase {

    @Transactional
    override fun registerPayment(eventId: Long, userId: Long, ticketId: Long): Long {
        val paymentEntity = paymentJpaRepository.save(
            PaymentEntity(
                eventId = eventId,
                userId = userId,
                ticketId = ticketId,
            )
        )
        paymentHistoryJpaRepository.save(
            PaymentHistoryEntity(
                paymentId = paymentEntity.id!!,
                eventId = eventId,
                userId = userId,
                ticketId = ticketId,
                status = PaymentStatus.CREATED,
            )
        )
        return paymentEntity.id!!
    }
}