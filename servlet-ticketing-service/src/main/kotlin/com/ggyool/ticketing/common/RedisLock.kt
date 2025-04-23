package com.ggyool.ticketing.common

import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

val redissonClient: RedissonClient by lazy {
    ApplicationContextProvider.getBean(RedissonClient::class.java)
}

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
            lock.unlock()
        }
    }
    return null
}

