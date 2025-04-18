package com.ggyool.event

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class EventApplication

fun main(args: Array<String>) {
    runApplication<EventApplication>(*args)
}
