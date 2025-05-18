package com.ggyool.user.application.service

import org.springframework.stereotype.Service

@Service
class UserCheckService {

    fun isActiveUser(userId: Long): Boolean {
        // 임의로 홀수 아이디 유저만 정상 유저라고 가정
        return userId % 2 == 1L
    }

    fun isFraudulentUser(userId: Long): Boolean {
        // 0으로 끝나는 유저는 이상 거래로 탐지된 사용자라고 가정
        return userId % 10 == 0L
    }
}