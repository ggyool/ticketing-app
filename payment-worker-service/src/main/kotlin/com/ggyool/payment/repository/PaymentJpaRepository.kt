package com.ggyool.payment.repository

import com.ggyool.payment.repository.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long>