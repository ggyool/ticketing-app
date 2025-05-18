package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.ProcessTicketingUsecase
import com.ggyool.ticketing.exception.TicketingAppException
import com.ggyool.ticketing.helper.produceWithDlt
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

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
            "[eventId: ${eventId}] userId=${userId} 유저의 예약 내역이 존재하지 않습니다",
        )
        val request = ProcessTicketingRequest(
            eventId = eventId,
            userId = userId,
            ticketId = ticketId,
            point = processTicketingInput.point,
            paymentInfo = processTicketingInput.paymentInfo
        )
        produceWithDlt("ticketing.process", UUID.randomUUID().toString(), request)
    }

    data class ProcessTicketingRequest(
        val eventId: Long,
        val userId: Long,
        val ticketId: String,
        val point: Long,
        val paymentInfo: String,
    )

    companion object {
        private const val TICKETING_PAYMENT_WAITING_KEY = "ticketing:%s:payment:waiting"
    }
}