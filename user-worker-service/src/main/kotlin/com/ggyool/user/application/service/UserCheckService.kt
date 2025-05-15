package com.ggyool.user.application.service

import org.springframework.stereotype.Service

@Service
class UserCheckService {

    fun isActiveUser(userId: Long): Boolean {
        // 임의로 홀수 아이디 유저만 정상 유저라고 가정
        return userId % 2 == 1L
    }
}