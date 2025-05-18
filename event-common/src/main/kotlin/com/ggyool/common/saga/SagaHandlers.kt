package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext

class SagaHandlers<T : SagaContext>(
    private val handlers: List<SagaHandler<T>>
) {

    init {
        assert(handlers.isNotEmpty(), { "handlers는 비어있을 수 없습니다" })
    }

    fun first(): SagaHandler<T> = handlers.first()

    fun last(): SagaHandler<T> = handlers.last()

    fun isFirstStep(stepName: String) = first().stepName == stepName

    fun isLastStep(stepName: String) = last().stepName == stepName

    fun previousHandler(stepName: String): SagaHandler<T> {
        if (isFirstStep(stepName)) {
            throw IllegalStateException("${stepName}이 처음 handler 입니다")
        }
        val curIdx = handlers.indexOfFirst { it.stepName == stepName }
        return handlers[curIdx - 1]
    }

    fun nextHandler(currentHandler: SagaHandler<T>): SagaHandler<T> {
        if (currentHandler.isLastStep()) {
            throw IllegalStateException("${currentHandler.stepName}이 마지막 handler 입니다")
        }
        val curIdx = handlers.indexOfFirst { it.stepName == currentHandler.stepName }
        return handlers[curIdx + 1]
    }

    fun findByStepName(stepName: String): SagaHandler<T> {
        return handlers.find { stepName == it.stepName }
            ?: throw IllegalStateException("handlers에 ${stepName}인 handler가 없습니다")
    }
}