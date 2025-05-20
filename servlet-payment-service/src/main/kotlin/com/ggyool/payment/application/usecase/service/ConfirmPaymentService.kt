package com.ggyool.payment.application.usecase.service

import com.ggyool.payment.application.usecase.ConfirmPaymentUsecase
import com.ggyool.payment.client.ExternalPaymentGatewayApi
import com.ggyool.payment.exception.PaymentAppException
import com.ggyool.payment.helper.produceWithDlt
import com.ggyool.payment.repository.PaymentJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

/**
 *  토스 페이 개발자 센터에서 결제 연동 API 설명 페이지 참고하여 흐름 따라갔음
 *  ProcessTicketing 사가의 완료를 주기적으로 확인하고 완료시 paymentId를 받아 이용한다.
 *  클라이언트 단에서 결제 UI로 넘어가고 유저가 결제 수단을 선택하고 결제 인증 요청을 한다.
 *  결제 인증 요청시 성공, 실패 콜백 url 을 함께 보내는데 성공시 성공 or 실패 url 로 콜백이 된다.
 *  결제 인증 한 방에 실제로 결제가 이뤄지는 건 아니고 최종 결제 승인 요청을 보내야 금액이 차감되는 흐름이다.
 *  success 콜백에서 아래 ConfirmPayment 호출한다고 가정하고 구현하였다.
 */
@Service
class ConfirmPaymentService(
    private val paymentRepository: PaymentJpaRepository,
    private val externalPaymentGatewayApi: ExternalPaymentGatewayApi
) : ConfirmPaymentUsecase {

    override fun confirmPayment(confirmPaymentInput: ConfirmPaymentUsecase.ConfirmPaymentInput)
            : ConfirmPaymentUsecase.ConfirmPaymentOutput {

        val paymentId = confirmPaymentInput.paymentId
        val amount = confirmPaymentInput.amount
        val pgPaymentId = confirmPaymentInput.pgPaymentId

        val payment = paymentRepository.findByIdOrNull(UUID.fromString(paymentId))
            ?: throw PaymentAppException(
                HttpStatus.NOT_FOUND,
                "[paymentId: ${paymentId}] payment가 존재하지 않습니다"
            )
        if (amount != payment.finalAmount) {
            throw PaymentAppException(
                HttpStatus.BAD_REQUEST,
                "[paymentId: ${paymentId}] 결제 승인 요청 금액인 ${amount}이 저장된 결제 금액인 ${payment.finalAmount}와 일치하지 않습니다"
            )
        }
        val succeeded = externalPaymentGatewayApi.confirm(
            ExternalPaymentGatewayApi.ExternalPaymentGatewayConfirmRequest(
                paymentKey = pgPaymentId,
                servicePaymentKey = paymentId,
                amount = amount,
            )
        )
        // payment-worker-service 쪽으로 PaymentEntity 수정하는 이벤트 발급
        val request = PaymentConfirmedRequest(
            paymentId = paymentId,
            pgPaymentId = pgPaymentId,
            confirmed = succeeded
        )
        produceWithDlt("payment.confirmed.request", UUID.randomUUID().toString(), request)
        return ConfirmPaymentUsecase.ConfirmPaymentOutput(
            paymentId = paymentId,
            pgPaymentId = pgPaymentId,
            succeeded = succeeded,
        )
    }

    data class PaymentConfirmedRequest(
        val paymentId: String,
        val pgPaymentId: String,
        val confirmed: Boolean,
    )
}