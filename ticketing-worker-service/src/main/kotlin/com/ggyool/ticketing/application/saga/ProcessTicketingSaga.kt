package com.ggyool.ticketing.application.saga

import com.ggyool.common.saga.EventBasedSaga
import com.ggyool.common.saga.SagaRepository
import com.ggyool.common.saga.model.SagaContextFactory
import com.ggyool.common.saga.model.SagaPayload
import com.ggyool.ticketing.application.saga.handler.CheckActiveUserHandler
import com.ggyool.ticketing.application.saga.handler.CheckFraudUserHandler
import com.ggyool.ticketing.application.saga.handler.PaymentHandler
import com.ggyool.ticketing.application.saga.handler.ReserveUserPointHandler
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProcessTicketingSaga(
    sagaContextRepository: SagaRepository<SagaContextEntity>,
    sagaContextFactory: SagaContextFactory<SagaContextEntity>,
    checkActiveUserHandler: CheckActiveUserHandler,
    reserveUserPointHandler: ReserveUserPointHandler,
    checkFraudUserHandler: CheckFraudUserHandler,
    paymentHandler: PaymentHandler,
) : EventBasedSaga<SagaContextEntity>(
    listOf(checkActiveUserHandler, reserveUserPointHandler, checkFraudUserHandler, paymentHandler),
    sagaContextRepository,
    sagaContextFactory
) {

    data class Payload(
        override val sagaId: UUID,
        override val referenceId: UUID,
        val eventId: Long,
        val userId: Long,
        val ticketId: UUID,
        val point: Long,
        var paymentId: UUID? = null,
    ) : SagaPayload {

        fun copyWithPaymentId(
            paymentId: UUID
        ): Payload {
            return Payload(
                sagaId = this.sagaId,
                referenceId = this.ticketId,
                eventId = this.eventId,
                userId = this.userId,
                ticketId = this.ticketId,
                point = this.point,
                paymentId = paymentId
            )
        }
    }
}