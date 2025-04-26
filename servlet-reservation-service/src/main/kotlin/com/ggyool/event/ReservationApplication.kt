package com.ggyool.event

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ReservationApplication

fun main(args: Array<String>) {
    runApplication<ReservationApplication>(*args)
}
