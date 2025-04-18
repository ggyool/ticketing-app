package com.ggyool.event.scenario

import com.ggyool.event.repository.EventJpaRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class EventServiceScenario {

    @Autowired
    private lateinit var eventJpaRepository: EventJpaRepository

    @Transactional
    @BeforeEach
    fun setUp() {
        eventJpaRepository.deleteAll()
        KafkaAdminUtils.clearTopics("event.create", "event.update")
    }

    // TODO: 추후 구현해야함
    @Test
    fun test() = runTest {
        val ids = mutableListOf<Long>()
        val iterator = EventEntityGenerator.generateEntities(10).iterator()
        for (entity in iterator) {
            val savedEntity = eventJpaRepository.save(entity)
            ids.add(savedEntity.id!!)
        }
        println(ids)
    }
}