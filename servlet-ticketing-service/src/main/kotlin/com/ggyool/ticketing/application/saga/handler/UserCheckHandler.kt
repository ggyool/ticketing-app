package com.ggyool.ticketing.application.saga.handler

import com.ggyool.common.saga.SagaHandler
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserCheckHandler(
    override val stepName: String = "user.check",
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : SagaHandler<SagaContextEntity> {

    override fun request(context: SagaContextEntity): SagaContextEntity {
        kafkaTemplate.send("${stepName}.request", UUID.randomUUID().toString(), context.payload)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        // NOOP
        return context
    }
}


// 1. 유저 체크
// 2. 결제