package com.ggyool.payment.client

import org.springframework.stereotype.Component

@Component
class ExternalPaymentGatewayApi {

    fun confirm(request: ExternalPaymentGatewayConfirmRequest): Boolean {
        // 외부 PG API 호출해서 최종 결제 승인 받았다고 가정
        return true
    }

    data class ExternalPaymentGatewayConfirmRequest(
        // PG사 결제 번호
        val paymentKey: String,
        // 서비스에서 사용하느 유니크 값
        val servicePaymentKey: String,
        val amount: Long
    )
}
