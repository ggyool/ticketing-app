package com.ggyool.ticketing.common

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val redissonClient: RedissonClient by lazy {
    ApplicationContextProvider.getBean(RedissonClient::class.java)
}

private val logger: Logger = LoggerFactory.getLogger("RedisLock")

fun <T> redisLock(
    keyGenerator: () -> String,
    waitMillis: Long = 1000,
    releaseMillis: Long = 1000,
    block: () -> T
): T? {
    val lock = redissonClient.getLock(keyGenerator())
    if (lock.tryLock(waitMillis, releaseMillis, TimeUnit.MILLISECONDS)) {
        try {
            return block()
        } finally {
            unlock(lock)
        }
    }
    return null
}

private fun unlock(lock: RLock) {
    try {
        lock.unlock()
    } catch (ex: IllegalMonitorStateException) {
        logger.info("[already unlocked] ${ex.message}")
    }
}

