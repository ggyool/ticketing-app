package com.ggyool.user.application.service

import org.springframework.stereotype.Service

@Service
class UserPointService {

    fun reservePoint(userId: Long, point: Long): Boolean {
        // 1000 포인트를 가지고 있다고 가정하고 임의로 구현
        if (point > 1000L) {
            return false
        }
        return true
    }

    fun restorePoint(userId: Long, point: Long) {
        // 항상 성공한다고 가정
    }
}