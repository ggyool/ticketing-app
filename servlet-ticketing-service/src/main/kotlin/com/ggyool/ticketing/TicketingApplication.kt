package com.ggyool.ticketing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class TicketingApplication

fun main(args: Array<String>) {
    runApplication<TicketingApplication>(*args)
}
