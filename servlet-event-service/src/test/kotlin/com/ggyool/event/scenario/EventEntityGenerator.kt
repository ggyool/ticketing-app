package com.ggyool.event.scenario

import com.ggyool.event.repository.entity.EventEntity
import kotlin.random.Random

object EventEntityGenerator {

    fun generateEntities(size: Int) = sequence {
        repeat(size) {
            yield(generateEntity())
        }
    }

    private fun generateEntity() = EventEntity(
        name = "name",
        ticketQuantity = randomTicketQuantity(),
    )

    private fun randomTicketQuantity(): Long = Random.nextLong(1000L, 50000L)
}