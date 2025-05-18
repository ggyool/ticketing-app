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
class CheckActiveUserHandler(
    override val stepName: String = "user.check.active",
    private val objectMapper: ObjectMapper
) : SagaHandler<SagaContextEntity> {

    override fun proceed(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = CheckActiveUserRequest(
            sagaId = payload.sagaId.toString(),
            userId = payload.userId
        )
        produceWithDlt("${stepName}.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        // NOOP
        return context
    }

    override fun needsCompensate(): Boolean = false

    override fun onProceed(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaProceedResult<SagaContextEntity> {
        val response =
            objectMapper.readValue(sagaResponse.payload, CheckActiveUserResponse::class.java)
        return if (response.isActive) {
            SagaProceedResult(context, SagaProceedResult.ProceedState.CONTINUE)
        } else {
            SagaProceedResult(context, SagaProceedResult.ProceedState.ROLLBACK)
        }
    }

    override fun onCompensate(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaContextEntity {
        println("무언가")
        return context
    }

    override fun isFirstStep(): Boolean = true

    data class CheckActiveUserRequest(
        val sagaId: String,
        val userId: Long
    )

    data class CheckActiveUserResponse(
        val userId: Long,
        val isActive: Boolean,
    )
}