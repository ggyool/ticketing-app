package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.ReserveTicketingUsecase
import com.ggyool.ticketing.exception.TicketingAppException
import com.ggyool.ticketing.helper.redisLock
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

/**
 *  티케팅 예약 로직
 *  티케팅 도메인은 순간 요청이 몰린다고 생각하여 레디스만 단일로 이용하여 티켓을 선점하는 작업을 먼저 진행
 *  요청 성공시 클라이언트에서는 ticketId를 받아서 다음 티케팅 프로세스에 사용
 *  티켓 도메인이 커머스에서 주문 도메인 같은 역할을 하는데 티켓 엔티티는 결제까지 완료한 이후에 생성하고 싶어서 그렇게 구현하였음
 */
@Service
class ReserveTicketingService(
    private val stringRedisTemplate: StringRedisTemplate,
) : ReserveTicketingUsecase {

    override fun reserveTicketing(reserveTicketingInput: ReserveTicketingUsecase.ReserveTicketingInput)
            : ReserveTicketingUsecase.ReserveTicketingOutput {

        val eventId = reserveTicketingInput.eventId
        val userId = reserveTicketingInput.userId
        validateDuplicatedReservation(eventId, userId.toString())
        try {
            return ReserveTicketingUsecase.ReserveTicketingOutput(
                checkAndReserveTicketing(eventId, userId.toString())!!
            )
        } catch (ex: Exception) {
            rollbackReserveTicketing(eventId, userId.toString())
            throw TicketingAppException(
                HttpStatus.BAD_REQUEST,
                "[eventId: ${eventId}, userId: ${userId}] redis 작업 중 문제 발생"
            )
        }
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

    private fun checkAndReserveTicketing(eventId: Long, userId: String): String? = redisLock(
        keyGenerator = { TICKETING_RESERVATION_LOCK_KEY.format(eventId) },
        waitMillis = 2000,
        releaseMillis = 2000,
    ) {
        validateTicketQuantity(eventId)

        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        val ticketId = UUID.randomUUID().toString()
        opsForHash.put(paymentWaitingKey, userId, ticketId)
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
        ticketId
    }

    private fun rollbackReserveTicketing(eventId: Long, userId: String) {
        val opsForHash = stringRedisTemplate.opsForHash<String, String>()
        val paymentWaitingKey = TICKETING_PAYMENT_WAITING_KEY.format(eventId)
        opsForHash.delete(paymentWaitingKey, userId)
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
        private const val APPLY_FIELD_COUNT = "1"

        private const val PAYMENT_WAITING_EXPIRE_SECOND = 600L
    }
}