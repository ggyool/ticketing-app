package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.ReserveTicketUsecase
import com.ggyool.ticketing.common.redisLock
import com.ggyool.ticketing.exception.TicketingAppException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ReserveTicketService(
    private val stringRedisTemplate: StringRedisTemplate,
) : ReserveTicketUsecase {

    override fun reserveTicket(reserveTicketInput: ReserveTicketUsecase.ReserveTicketInput) {
        val eventId = reserveTicketInput.eventId
        val userId = reserveTicketInput.userId.toString()
        validateDuplicatedReservation(eventId, userId)
        checkAndReserveTicket(eventId, userId)
    }

    private fun validateDuplicatedReservation(eventId: Long, userId: String) {
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val paymentCompletedKey = TICKETING_PAYMENT_COMPLETED_KEY.format(eventId)
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        if (opsForHash.hasKey(paymentWaitingKey, userId)) {
            throw TicketingAppException(
                HttpStatus.CONFLICT,
                "[eventId: ${eventId}, userId: ${userId}] 이미 결제 대기 중입니다"
            )
        }
        if (opsForHash.hasKey(paymentCompletedKey, userId)) {
            throw TicketingAppException(
                HttpStatus.CONFLICT,
                "[eventId: ${eventId}, userId: ${userId}] 이미 예매한 티켓이 있습니다"
            )
        }
    }

    private fun checkAndReserveTicket(eventId: Long, userId: String) = redisLock(
        keyGenerator = { TICKETING_RESERVATION_LOCK_KEY.format(eventId) },
        waitMillis = 2000,
        releaseMillis = 2000,
    ) {
        validateTicketQuantity(eventId)

        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        opsForHash.put(paymentWaitingKey, userId, EMPTY_STRING)
        val script = """
            local res = redis.call("HEXPIRE", KEYS[1], ARGV[1], "FIELDS", "$APPLY_FIELD_COUNT", ARGV[2])
            return res
        """.trimIndent()
        stringRedisTemplate.execute(
            DefaultRedisScript(script, ArrayList::class.java),
            listOf(paymentWaitingKey),
            PAYMENT_WAITING_EXPIRE_SECOND.toString(),
            userId,
        )
    }

    private fun validateTicketQuantity(eventId: Long) {
        val totalTicketQuantityKey = TICKETING_QUANTITY_KEY.format(eventId)
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val paymentCompletedKey = TICKETING_PAYMENT_COMPLETED_KEY.format(eventId)
        val opsForValue = stringRedisTemplate.opsForValue()
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()

        val totalTicketQuantity =
            opsForValue.get(totalTicketQuantityKey)?.toInt() ?: throw TicketingAppException(
                HttpStatus.BAD_REQUEST,
                "[eventId: ${eventId}] ${totalTicketQuantityKey} 가 존재하지 않습니다",
            )
        val waitingCount = opsForHash.size(paymentWaitingKey) ?: 0
        val completedCount = opsForHash.size(paymentCompletedKey) ?: 0
        if (waitingCount + completedCount >= totalTicketQuantity) {
            throw TicketingAppException(
                HttpStatus.BAD_REQUEST,
                "[eventId: ${eventId}] 남은 티켓 수량이 없습니다"
            )
        }
    }

    companion object {
        private const val TICKETING_RESERVATION_LOCK_KEY = "lock:ticketing:reservation:%s"
        private const val TICKETING_QUANTITY_KEY = "ticketing:quantity:%s"
        private const val TICKETING_PAYMENT_WAITING_KEY = "ticketing:%s:payment:waiting"
        private const val TICKETING_PAYMENT_COMPLETED_KEY = "ticketing:%s:payment:completed"
        private const val EMPTY_STRING = ""
        private const val APPLY_FIELD_COUNT = "1"

        private const val PAYMENT_WAITING_EXPIRE_SECOND = 600L
    }
}