package com.ggyool.payment.repository

import com.ggyool.payment.repository.entity.PaymentHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentHistoryJpaRepository : JpaRepository<PaymentHistoryEntity, Long>