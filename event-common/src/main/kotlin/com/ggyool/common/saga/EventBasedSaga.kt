package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaContextFactory
import com.ggyool.common.saga.model.SagaResponse

class EventBasedSaga<T : SagaContext>(
    private val handlers: SagaHandlers<T>,
    private val sagaRepository: SagaRepository<T>,
    private val sagaContextFactory: SagaContextFactory<T>,
) : Saga<T> {

    override fun start(): T {
        val sagaContext = sagaContextFactory.initContext()
        val processedSagaContext = handlers.first().request(sagaContext)
        return sagaRepository.save(processedSagaContext)
    }

    override fun process(sagaResponse: SagaResponse): T {
        val sagaId = sagaResponse.sagaId
        val sagaContext = sagaRepository.findByIdOrNull(sagaId)
            ?: throw IllegalStateException("SagaContext가 없습니다 (sagaId: $sagaId)")
        val currentStep = sagaContext.currentStep
        val processedSagaContext = if (sagaResponse.succeeded()) {
            if (handlers.isLastStep(currentStep)) {
                sagaContextFactory.completed(sagaContext, currentStep)
            } else {
                val nextHandler = handlers.nextHandler(currentStep)
                nextHandler.request(
                    sagaContextFactory.success(sagaContext, currentStep, nextHandler.stepName)
                )
            }
        } else {
            if (handlers.isFirstStep(currentStep)) {
                sagaContextFactory.compensated(sagaContext, currentStep)
            } else {
                val previousHandler = handlers.findByStepName(currentStep)
                previousHandler.compensate(
                    sagaContextFactory.compensating(sagaContext, currentStep, previousHandler.stepName)
                )
            }
        }
        return sagaRepository.save(processedSagaContext)
    }
}