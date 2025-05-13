package com.ggyool.ticketing.application.usecase.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.saga.model.SagaState
import com.ggyool.ticketing.application.saga.ProcessTicketingSaga
import com.ggyool.ticketing.application.usecase.ProcessTicketingUsecase
import com.ggyool.ticketing.exception.TicketingAppException
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProcessTicketingService(
    private val stringRedisTemplate: StringRedisTemplate,
    private val processTicketingSaga: ProcessTicketingSaga,
    private val objectMapper: ObjectMapper,
) : ProcessTicketingUsecase {

    override fun processTicketing(processTicketingInput: ProcessTicketingUsecase.ProcessTicketingInput) {
        val userId = processTicketingInput.userId
        val eventId = processTicketingInput.eventId
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        stringRedisTemplate.opsForHash<String, String>()
            .get(paymentWaitingKey, userId.toString()) ?: throw TicketingAppException(
            HttpStatus.BAD_REQUEST,
            "[eventId: ${eventId}] userId=${userId} 유저의 예약 내역이 존재하지 않습니다",
        )
        val payload = ProcessTicketingSaga.Payload(
            eventId = processTicketingInput.eventId,
            userId = processTicketingInput.userId,
            paymentInfo = processTicketingInput.paymentInfo
        )
        processTicketingSaga.start(
            SagaContextEntity(
                id = UUID.randomUUID(),
                version = 0L,
                sagaType = "processTicketing",
                payload = objectMapper.writeValueAsString(payload),
                currentStep = processTicketingSaga.firstStepName(),
                stepHistory = emptyList(),
                sagaState = SagaState.STARTED
            )
        )
        // TODO
        // 1. 대기열에 있는지 확인 + UUID 가져오기
        // 없으면 실패


        // 2. 있다면 -> UUID로 사가 생성
        // 이벤트 서비스로 예약 가능한 서비스인지 확인
        // 필요 정보 포함하여 payment 요청 커맨드 발급

        // 3. 결제 완료시 대기열에 있는지 확인
        // - 결제 완료 큐로 이동 and 티켓 발급
        // - 환불

    }

    companion object {
        private const val TICKETING_PAYMENT_WAITING_KEY = "ticketing:%s:payment:waiting"
    }
}