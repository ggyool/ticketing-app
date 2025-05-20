package com.ggyool.payment.repository

import com.ggyool.payment.repository.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentJpaRepository : JpaRepository<PaymentEntity, UUID>