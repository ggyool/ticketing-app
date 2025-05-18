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
class ReserveUserPointHandler(
    override val stepName: String = "user.reserve.point",
    private val objectMapper: ObjectMapper
) : SagaHandler<SagaContextEntity> {

    override fun proceed(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = ReserveUserPointRequest(
            sagaId = payload.sagaId.toString(),
            userId = payload.userId,
            point = payload.point
        )
        produceWithDlt("${stepName}.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = RestoreUserPointRequest(
            sagaId = payload.sagaId.toString(),
            userId = payload.userId,
            point = payload.point
        )
        produceWithDlt("user.restore.point.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun needsCompensate(): Boolean = true

    override fun onProceed(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaProceedResult<SagaContextEntity> {
        val response =
            objectMapper.readValue(sagaResponse.payload, ReserveUserPointResponse::class.java)
        return if (response.reserved) {
            SagaProceedResult(context, SagaProceedResult.ProceedState.CONTINUE)
        } else {
            SagaProceedResult(context, SagaProceedResult.ProceedState.ROLLBACK)
        }
    }

    override fun onCompensate(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaContextEntity {
        val response =
            objectMapper.readValue(sagaResponse.payload, RestoreUserPointResponse::class.java)
        return context
    }

    data class ReserveUserPointRequest(
        val sagaId: String,
        val userId: Long,
        val point: Long
    )

    data class ReserveUserPointResponse(
        val sagaId: String,
        val userId: Long,
        val point: Long,
        val reserved: Boolean,
    )

    data class RestoreUserPointRequest(
        val sagaId: String,
        val userId: Long,
        val point: Long
    )

    data class RestoreUserPointResponse(
        val sagaId: String,
        val userId: Long,
        val point: Long,
        val succeed: Boolean,
    )
}