package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.MovePaymentCompletedQueueUsecase
import com.ggyool.ticketing.application.usecase.MovePaymentCompletedQueueUsecase.MovePaymentCompletedQueueInput
import com.ggyool.ticketing.exception.TicketingWorkerException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service

@Service
class MovePaymentCompletedQueueService(
    private val stringRedisTemplate: StringRedisTemplate,
) : MovePaymentCompletedQueueUsecase {

    override fun movePaymentCompletedQueue(movePaymentCompletedQueueInput: MovePaymentCompletedQueueInput): Boolean {
        val eventId = movePaymentCompletedQueueInput.eventId
        val userId = movePaymentCompletedQueueInput.userId.toString()
        val ticketId = movePaymentCompletedQueueInput.ticketId.toString()
        // 이미 결제 완료 큐로 이동한 경우
        if (existsPaymentCompletedQueue(eventId, userId, ticketId)) {
            logger.warn(
                "[eventId: $eventId userId: $userId ticketId: $ticketId] 이미 결제 완료 큐에 존재합니다"
            )
            return true
        }
        // 예약 기한 만료 확인
        if (!existsPaymentWaitingQueue(eventId, userId, ticketId)) {
            return false
        }
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val paymentCompletedKey = TICKETING_PAYMENT_COMPLETED_KEY.format(eventId)
        val script = """
                local reservedTicketId = redis.call('HGET', KEYS[1], ARGV[1])
                if reservedTicketId then
                    redis.call('HSET', KEYS[2], ARGV[1], reservedTicketId)
                    redis.call('HDEL', KEYS[1], ARGV[1])
                    return "true"
                else
                    return "false"
                end
            """.trimIndent()

        val result: String = stringRedisTemplate.execute(
            DefaultRedisScript(script, String::class.java),
            listOf(paymentWaitingKey, paymentCompletedKey),
            userId,
            ticketId
        )
        return result == "true"
    }

    private fun existsPaymentWaitingQueue(
        eventId: Long,
        userId: String,
        ticketId: String
    ): Boolean {
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        val reservedTicketId = opsForHash.get(paymentWaitingKey, userId) ?: return false
        if (ticketId != reservedTicketId) {
            throw TicketingWorkerException(
                "결제 완료된 ticketId: $ticketId 가 예약된 reservedTicketId: $reservedTicketId 와 다릅니다"
            )
        }
        return true
    }

    private fun existsPaymentCompletedQueue(
        eventId: Long,
        userId: String,
        ticketId: String
    ): Boolean {
        val paymentCompletedKey = TICKETING_PAYMENT_COMPLETED_KEY.format(eventId)
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        val reservedTicketId = opsForHash.get(paymentCompletedKey, userId) ?: return false
        if (ticketId != reservedTicketId) {
            throw TicketingWorkerException(
                "결제 완료된 ticketId: $ticketId 가 기존 결제 처리된 reservedTicketId: $reservedTicketId 와 다릅니다"
            )
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)

        private const val TICKETING_PAYMENT_WAITING_KEY = "ticketing:%s:payment:waiting"
        private const val TICKETING_PAYMENT_COMPLETED_KEY = "ticketing:%s:payment:completed"
    }
}