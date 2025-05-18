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
class PaymentHandler(
    override val stepName: String = "payment",
    private val objectMapper: ObjectMapper,
    private val sagaContextFactory: SagaContextEntityFactory
) : SagaHandler<SagaContextEntity> {

    override fun proceed(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = PaymentRequest(
            sagaId = payload.sagaId.toString(),
            userId = payload.userId,
            eventId = payload.eventId,
            paymentInfo = payload.paymentInfo
        )
        produceWithDlt("${stepName}.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun compensate(context: SagaContextEntity): SagaContextEntity {
        val payload = objectMapper.readValue<ProcessTicketingSaga.Payload>(context.payload)
        val request = PaymentCompensateRequest(
            sagaId = payload.sagaId.toString(),
            paymentId = payload.paymentId!!
        )
        produceWithDlt("payment.compensate.request", UUID.randomUUID().toString(), request)
        return context
    }

    override fun needsCompensate(): Boolean = true

    override fun onProceed(
        context: SagaContextEntity,
        sagaResponse: SagaResponse
    ): SagaProceedResult<SagaContextEntity> {
        val response = objectMapper.readValue(sagaResponse.payload, PaymentResponse::class.java)
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
            objectMapper.readValue(sagaResponse.payload, PaymentCompensateResponse::class.java)
        println("무언가")
        return context
    }

    override fun isLastStep(): Boolean = true

    data class PaymentRequest(
        val sagaId: String,
        val eventId: Long,
        val userId: Long,
        val paymentInfo: String,
    )

    data class PaymentResponse(
        val sagaId: String,
        val succeed: Boolean,
        val userId: Long,
        val eventId: Long,
        val paymentId: UUID,
    )

    data class PaymentCompensateRequest(
        val sagaId: String,
        val paymentId: UUID,
    )

    data class PaymentCompensateResponse(
        val sagaId: String,
        val succeed: Boolean,
        val paymentId: UUID,
    )
}