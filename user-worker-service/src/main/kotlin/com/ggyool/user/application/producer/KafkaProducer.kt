package com.ggyool.user.application.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.user.application.service.EventLogService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class KafkaProducer(
    @Qualifier("kafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val eventLogService: EventLogService,
) {

    @Transactional
    fun send(topic: String, key: String, payload: Any) {
        eventLogService.save(UUID.fromString(key))
        // TODO
        // 에러 터지면 eventLog 롤백되나 확인
        kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload))
    }
}