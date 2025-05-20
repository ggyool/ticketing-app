package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.ProcessTicketingUsecase
import com.ggyool.ticketing.exception.TicketingAppException
import com.ggyool.ticketing.helper.produceWithDlt
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

/**
 *  티케팅 진행 로직
 *  티켓 선점 이후에 해당 로직 호출
 *  처음엔 해당 프로세서스에서 결제가 이뤄진다고 생각했는데 UI와 함께 생각해보니 불가능함
 *  여러 검증 과정 이후 Payment 엔티티 CREATED 로 생성까지만 수행
 */
@Service
class ProcessTicketingService(
    private val stringRedisTemplate: StringRedisTemplate,
) : ProcessTicketingUsecase {

    override fun processTicketing(processTicketingInput: ProcessTicketingUsecase.ProcessTicketingInput) {
        val userId = processTicketingInput.userId
        val eventId = processTicketingInput.eventId
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val ticketId = stringRedisTemplate.opsForHash<String, String>()
            .get(paymentWaitingKey, userId.toString()) ?: throw TicketingAppException(
            HttpStatus.BAD_REQUEST,
            "[eventId: ${eventId} userId=${userId}] 유저의 예약 내역이 존재하지 않습니다",
        )
        if (processTicketingInput.ticketId != ticketId) {
            throw TicketingAppException(
                HttpStatus.BAD_REQUEST,
                "[eventId: ${eventId} userId=${userId} ticketId=${processTicketingInput.ticketId}] 유효하지 않은 접근입니다 예매된 티켓 아이디와 다릅니다",
            )
        }
        val request = ProcessTicketingRequest(
            eventId = eventId,
            userId = userId,
            ticketId = ticketId,
            totalAmount = processTicketingInput.totalAmount,
            point = processTicketingInput.point,
        )
        // ticketing-worker-service 에서 해당 토픽 구독하여 사가 실행
        produceWithDlt("ticketing.process", UUID.randomUUID().toString(), request)
    }

    data class ProcessTicketingRequest(
        val eventId: Long,
        val userId: Long,
        val ticketId: String,
        val totalAmount: Long,
        val point: Long,
    )

    companion object {
        private const val TICKETING_PAYMENT_WAITING_KEY = "ticketing:%s:payment:waiting"
    }
}