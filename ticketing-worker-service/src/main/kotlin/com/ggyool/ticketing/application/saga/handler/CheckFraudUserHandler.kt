package com.ggyool.ticketing.application.saga.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.common.saga.SagaHandler
import com.ggyool.common.saga.model.SagaProceedResult
import com.ggyool.common.saga.model.SagaResponse
import com.ggyool.ticketing.application.saga.ProcessTicketingSaga
import com.ggyool.ticketing.helper.produceWithDlt
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class CheckFraudUserHandler(
    override val stepName: String = "user.check.fraud",
    private val objectMapper: ObjectMapper
) : SagaHandler<SagaContextEntity> {

    override fun proceed(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = CheckFraudUserRequest(
            sagaId = payload.sagaId.toString(),
            userId = payload.userId
        )
        produceWithDlt("${stepName}.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        throw IllegalStateException()
    }

    override fun needsCompensate(): Boolean = false

    override fun onProceed(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaProceedResult<SagaContextEntity> {
        val response =
            objectMapper.readValue(sagaResponse.payload, CheckFraudUserResponse::class.java)
        return if (response.isFraudulent) {
            SagaProceedResult(context, SagaProceedResult.ProceedState.ROLLBACK)
        } else {
            SagaProceedResult(context, SagaProceedResult.ProceedState.CONTINUE)
        }
    }

    override fun onCompensate(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaContextEntity {
        return context
    }

    data class CheckFraudUserRequest(
        val sagaId: String,
        val userId: Long
    )

    data class CheckFraudUserResponse(
        val userId: Long,
        val isFraudulent: Boolean,
    )
}