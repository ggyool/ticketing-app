package com.ggyool.payment.application.usecase.service

import com.ggyool.payment.application.usecase.FailPaymentUsecase
import com.ggyool.payment.exception.PaymentWorkerException
import com.ggyool.payment.repository.PaymentHistoryJpaRepository
import com.ggyool.payment.repository.PaymentJpaRepository
import com.ggyool.payment.repository.entity.PaymentHistoryEntity
import com.ggyool.payment.repository.entity.PaymentStatus
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class FailPaymentService(
    private val paymentRepository: PaymentJpaRepository,
    private val paymentHistoryRepository: PaymentHistoryJpaRepository
) : FailPaymentUsecase {

    @Transactional
    override fun failPayment(paymentId: UUID, pgPaymentId: String) {
        val payment = paymentRepository.findByIdOrNull(paymentId)
            ?: throw PaymentWorkerException("[paymentId: ${paymentId}] payment가 존재하지 않습니다")
        payment.fillPgPaymentId(pgPaymentId)
        payment.failed()
        paymentHistoryRepository.save(
            PaymentHistoryEntity(
                paymentId = payment.id,
                status = PaymentStatus.FAILED,
            )
        )
    }
}