package com.ggyool.ticketing.application.saga.handler

import com.ggyool.common.saga.SagaHandler
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

// TODO
@Component
class PaymentHandler(
    override val stepName: String = "payment",
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : SagaHandler<SagaContextEntity> {

    override fun request(context: SagaContextEntity): SagaContextEntity {
        // TODO payment request
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        // TODO
        // TODO payment compensate
        return context
    }
}