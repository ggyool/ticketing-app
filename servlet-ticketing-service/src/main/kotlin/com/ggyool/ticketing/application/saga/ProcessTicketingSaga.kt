package com.ggyool.ticketing.application.saga

import com.ggyool.common.saga.EventBasedSaga
import com.ggyool.common.saga.SagaHandler
import com.ggyool.common.saga.SagaRepository
import com.ggyool.common.saga.model.SagaContextFactory
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.stereotype.Component

@Component
class ProcessTicketingSaga(
    sagaContextRepository: SagaRepository<SagaContextEntity>,
    sagaContextFactory: SagaContextFactory<SagaContextEntity>,
    eventCheckHandler: SagaHandler<SagaContextEntity>
) : EventBasedSaga<SagaContextEntity>(
    listOf(eventCheckHandler),
    sagaContextRepository,
    sagaContextFactory
) {

    data class Payload(
        val eventId: Long,
        val userId: Long,
        val paymentInfo: String,
    )
}