package com.ggyool.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class PaymentWorkerApplication

fun main(args: Array<String>) {
    runApplication<PaymentWorkerApplication>(*args)
}
