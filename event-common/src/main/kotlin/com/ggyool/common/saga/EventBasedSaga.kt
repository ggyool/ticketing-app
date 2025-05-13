package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaContextFactory
import com.ggyool.common.saga.model.SagaResponse

abstract class EventBasedSaga<T : SagaContext>(
    handlerList: List<SagaHandler<T>>,
    private val sagaRepository: SagaRepository<T>,
    private val sagaContextFactory: SagaContextFactory<T>,
) : Saga<T> {

    private val handlers: SagaHandlers<T> = SagaHandlers(handlerList)

    override fun start(initialContext: T): T {
        val processedSagaContext = handlers.first().request(initialContext)
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
                    sagaContextFactory.compensating(
                        sagaContext,
                        currentStep,
                        previousHandler.stepName
                    )
                )
            }
        }
        return sagaRepository.save(processedSagaContext)
    }

    override fun firstStepName() = handlers.first().stepName
}