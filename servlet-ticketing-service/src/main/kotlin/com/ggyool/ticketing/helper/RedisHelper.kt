package com.ggyool.ticketing.helper

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val redissonClient: RedissonClient by lazy {
    ApplicationContextProvider.getBean(RedissonClient::class.java)
}

private val logger: Logger = LoggerFactory.getLogger("RedisHelper")

class LockAcquisitionFailedException(
    lockKey: String
) : RuntimeException("[lockKey: $lockKey] 락 획득에 실패했습니다")

fun <T> redisLock(
    keyGenerator: () -> String,
    waitMillis: Long = 1000,
    releaseMillis: Long = 1000,
    block: () -> T
): T {
    val lockKey = keyGenerator()
    val lock = redissonClient.getLock(lockKey)
    if (lock.tryLock(waitMillis, releaseMillis, TimeUnit.MILLISECONDS)) {
        try {
            return block()
        } finally {
            unlock(lock)
        }
    }
    throw LockAcquisitionFailedException(lockKey)
}

private fun unlock(lock: RLock) {
    try {
        lock.unlock()
    } catch (ex: IllegalMonitorStateException) {
        logger.info("[already unlocked] ${ex.message}")
    }
}