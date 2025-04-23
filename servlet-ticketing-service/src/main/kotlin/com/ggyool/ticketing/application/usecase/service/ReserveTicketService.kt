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

    // TODO: remove
    fun reserveTicketV1(reserveTicketInput: ReserveTicketUsecase.ReserveTicketInput) {
        val eventId = reserveTicketInput.eventId
        val redisKey = TICKETING_QUANTITY_KEY.format(eventId)
        // TODO: MEMO
        // count 먼저 체크하고 decr 하는 동작은 원자적이지 않기 때문에 분산 환경에서는 일관성이 깨지게 됨
        // 감소만 필요하다면 decr 만 수행하여 return 값이 0보다 작은지만 살펴봐도 유효한 예약인지 알 수 있는데
        // 지금 상황은 10분 선점 후 결제하지 않으면 예약을 취소시키고 수량을 올려야해서 lua script로 묶는 방법을 사용했다.
        // 카운트 줄인 후에 결제 대기 이벤트 만드려고 했으나 결제 대기시간이 지나도 결제 하지 않은 경우에 다시 카운트를 올려줘야하는데 까다롭다.
        // keyspace notification 써보려다가 서버가 계속 살아있어야하는 이슈가 있고
        // 레디스 리스트를 큐처럼 사용하여 주기적으로 바라보면서 만료 체크하려고 했으나 위의 구현이 더 깔끔할 것 같아서 바꿈
        val script = """
            local count = redis.call("GET", KEYS[1])
            if tonumber(count) <= 0 then
                return -999
            end
            return redis.call("DECR", KEYS[1])
        """.trimIndent()
        val remainTicket = stringRedisTemplate.execute(
            DefaultRedisScript(script, Long::class.java),
            listOf(redisKey)
        )
        if (remainTicket == 999L) {
            throw TicketingAppException(
                HttpStatus.CONFLICT,
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