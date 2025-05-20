package com.ggyool.payment.application.usecase.service

import com.ggyool.payment.application.usecase.RemovePaymentUsecase
import com.ggyool.payment.repository.PaymentHistoryJpaRepository
import com.ggyool.payment.repository.PaymentJpaRepository
import com.ggyool.payment.repository.entity.PaymentHistoryEntity
import com.ggyool.payment.repository.entity.PaymentStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RemovePaymentService(
    private val paymentRepository: PaymentJpaRepository,
    private val paymentHistoryRepository: PaymentHistoryJpaRepository
) : RemovePaymentUsecase {

    @Transactional
    override fun removePayment(paymentId: UUID) {
        paymentRepository.deleteById(paymentId)
        paymentHistoryRepository.save(
            PaymentHistoryEntity(
                paymentId = paymentId,
                status = PaymentStatus.DELETED,
            )
        )
    }
}