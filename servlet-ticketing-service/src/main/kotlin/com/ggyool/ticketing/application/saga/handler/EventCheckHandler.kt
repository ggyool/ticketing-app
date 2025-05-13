package com.ggyool.ticketing.application.saga.handler

import com.ggyool.common.saga.SagaHandler
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class EventCheckHandler(
    override val stepName: String = "event.check",
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : SagaHandler<SagaContextEntity> {

    override fun request(context: SagaContextEntity): SagaContextEntity {
        // TODO
        // event.check.request
//        kafkaTemplate.send("${stepName}.request", context.payload)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        // NOOP
        return context
    }
}