package com.ggyool.common.saga

import com.ggyool.common.saga.model.*

abstract class EventBasedSaga<T : SagaContext>(
    handlerList: List<SagaHandler<T>>,
    private val sagaRepository: SagaRepository<T>,
    private val sagaContextFactory: SagaContextFactory<T>,
) : Saga<T> {

    private val handlers: SagaHandlers<T> = SagaHandlers(handlerList)

    // 사가를 트리거링 하는 메서드
    override fun start(sagaType: String, payload: SagaPayload): T {
        val initialSagaContext =
            sagaContextFactory.started(sagaType, payload, handlers.first().stepName)
        val processedSagaContext = handlers.first().proceed(initialSagaContext)
        return sagaRepository.save(processedSagaContext)
    }

    override fun onResponse(sagaResponse: SagaResponse): T {
        val sagaId = sagaResponse.sagaId
        val sagaContext = sagaRepository.findByIdOrNull(sagaId)
            ?: throw IllegalStateException("SagaContext가 없습니다 (sagaId: $sagaId)")

        val currentHandler = handlers.findByStepName(sagaContext.currentStep)
        val processedSagaContext = if (sagaContext.sagaState == SagaState.COMPENSATING) {
            previous(sagaResponse, sagaContext, currentHandler)
        } else {
            val sagaProceedResult = currentHandler.onProceed(sagaContext, sagaResponse)
            if (sagaProceedResult.isContinue()) {
                next(sagaProceedResult.sagaContext, currentHandler)
            } else {
                previous(sagaResponse, sagaProceedResult.sagaContext, currentHandler)
            }
        }
        return sagaRepository.save(processedSagaContext)
    }

    private fun next(sagaContext: T, currentHandler: SagaHandler<T>): T {
        return if (currentHandler.isLastStep()) {
            sagaContextFactory.completed(sagaContext, currentHandler.stepName)
        } else {
            val nextHandler = handlers.nextHandler(currentHandler)
            nextHandler.proceed(
                sagaContextFactory.success(
                    sagaContext,
                    currentHandler.stepName,
                    nextHandler.stepName
                )
            )
        }
    }

    private fun previous(
        sagaResponse: SagaResponse,
        sagaContext: T,
        failedHandler: SagaHandler<T>
    ): T {
        var currentHandler = failedHandler
        var currentSagaContext = sagaContext
        while (!currentHandler.isFirstStep()) {
            currentSagaContext = currentHandler.onCompensate(currentSagaContext, sagaResponse)
            val previousHandler = handlers.previousHandler(currentHandler.stepName)
            if (previousHandler.needsCompensate()) {
                return previousHandler.compensate(
                    sagaContextFactory.compensating(
                        currentSagaContext,
                        currentHandler.stepName,
                        previousHandler.stepName
                    )
                )
            } else {
                currentSagaContext = sagaContextFactory.compensating(
                    currentSagaContext,
                    currentHandler.stepName,
                    previousHandler.stepName
                )
            }
            currentHandler = previousHandler
        }
        currentSagaContext = currentHandler.onCompensate(currentSagaContext, sagaResponse)
        return sagaContextFactory.compensated(currentSagaContext, currentHandler.stepName)
    }
}