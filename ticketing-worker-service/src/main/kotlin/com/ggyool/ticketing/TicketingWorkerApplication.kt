package com.ggyool.ticketing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TicketingWorkerApplication

fun main(args: Array<String>) {
    runApplication<TicketingWorkerApplication>(*args)
}
