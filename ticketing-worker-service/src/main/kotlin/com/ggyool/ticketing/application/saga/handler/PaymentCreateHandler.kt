package com.ggyool.ticketing.application.saga.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.common.saga.SagaHandler
import com.ggyool.common.saga.model.SagaProceedResult
import com.ggyool.common.saga.model.SagaResponse
import com.ggyool.ticketing.application.saga.ProcessTicketingSaga
import com.ggyool.ticketing.application.saga.SagaContextEntityFactory
import com.ggyool.ticketing.helper.produceWithDlt
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentCreateHandler(
    override val stepName: String = "payment.create",
    private val objectMapper: ObjectMapper,
    private val sagaContextFactory: SagaContextEntityFactory
) : SagaHandler<SagaContextEntity> {

    override fun proceed(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = PaymentCreateRequest(
            sagaId = payload.sagaId.toString(),
            ticketId = payload.ticketId.toString(),
            userId = payload.userId,
            eventId = payload.eventId,
        )
        produceWithDlt("${stepName}.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = PaymentDeleteRequest(
            sagaId = payload.sagaId.toString(),
            paymentId = payload.paymentId!!
        )
        produceWithDlt("payment.delete.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun needsCompensate(): Boolean = true

    override fun onProceed(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaProceedResult<SagaContextEntity> {
        val response =
            objectMapper.readValue(sagaResponse.payload, PaymentCreateResponse::class.java)
        val payload =
            objectMapper.readValue(context.payload, ProcessTicketingSaga.Payload::class.java)
        val newPayload =
            objectMapper.writeValueAsString(payload.copyWithPaymentId(response.paymentId))
        return if (response.succeed) {
            SagaProceedResult(
                sagaContextFactory.copyWhenNonNullArgs(context, payload = newPayload),
                SagaProceedResult.ProceedState.CONTINUE
            )
        } else {
            SagaProceedResult(context, SagaProceedResult.ProceedState.ROLLBACK)
        }
    }

    override fun onCompensate(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaContextEntity {
        val response =
            objectMapper.readValue(sagaResponse.payload, PaymentDeleteResponse::class.java)
        // 하고 싶은 동작이 있는 경우 넣으면 보상 응답을 받은 이후 실행
        return context
    }

    override fun isLastStep(): Boolean = true

    data class PaymentCreateRequest(
        val sagaId: String,
        val ticketId: String,
        val eventId: Long,
        val userId: Long,
    )

    data class PaymentCreateResponse(
        val sagaId: String,
        val succeed: Boolean,
        val userId: Long,
        val eventId: Long,
        val paymentId: UUID,
    )

    data class PaymentDeleteRequest(
        val sagaId: String,
        val paymentId: UUID,
    )

    data class PaymentDeleteResponse(
        val sagaId: String,
        val succeed: Boolean,
        val paymentId: UUID,
    )
}