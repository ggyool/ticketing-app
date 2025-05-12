package com.ggyool.common.saga.model

import java.util.*

interface SagaContextFactory<T : SagaContext> {

    fun initContext(): T

    fun createWithAllArgs(
        id: UUID,
        version: Long,
        sagaType: String,
        payload: String?,
        currentStep: String,
        stepHistory: List<SagaStep>,
        sagaState: SagaState,
    ): T

    fun copyWhenNonNullArgs(
        sagaContext: T,
        id: UUID? = null,
        version: Long? = null,
        sagaType: String? = null,
        payload: String? = null,
        currentStep: String? = null,
        stepHistory: List<SagaStep>? = null,
        sagaState: SagaState? = null,
    ): T {
        return createWithAllArgs(
            id = id ?: sagaContext.id,
            version = version ?: sagaContext.version,
            sagaType = sagaType ?: sagaContext.sagaType,
            payload = payload ?: sagaContext.payload,
            currentStep = currentStep ?: sagaContext.currentStep,
            stepHistory = stepHistory ?: sagaContext.stepHistory,
            sagaState = sagaState ?: sagaContext.sagaState,
        )
    }

    fun completed(sagaContext: T, currentStep: String): T {
        // 전체 사가 성공 완료 처리
        val newSagaState = SagaState.COMPLETED
        // 현재 스텝 성공 처리
        succeedCurrentStep(sagaContext.stepHistory, currentStep)
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            sagaState = newSagaState,
        )
    }

    fun success(sagaContext: T, currentStep: String, nextStep: String): T {
        // 현재 스텝 성공 처리
        succeedCurrentStep(sagaContext.stepHistory, currentStep)
        // 새로운 스텝 넣기
        val newStepHistory = sagaContext.stepHistory.toMutableList().apply {
            add(SagaStep(nextStep, StepState.STARTED))
        }
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            currentStep = nextStep,
            stepHistory = newStepHistory,
        )
    }

    fun compensated(sagaContext: T, currentStep: String): T {
        // 전체 사가 보상 완료 처리
        val newSagaState = SagaState.COMPENSATED
        // 현재 스텝 실패 또는 보상됨 처리
        cancelStep(sagaContext.stepHistory, currentStep)
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            sagaState = newSagaState,
        )
    }

    fun compensating(sagaContext: T, currentStep: String, previousStep: String): T {
        // 전체 사가 보상중 처리
        val newSagaState = SagaState.COMPENSATING
        // 현재 스텝 실패 또는 보상됨 처리
        cancelStep(sagaContext.stepHistory, currentStep)
        // 이전 스텝 보상중 처리
        cancelStep(sagaContext.stepHistory, previousStep)
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            currentStep = previousStep,
            sagaState = newSagaState,
        )
    }

    fun succeedCurrentStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ) {
        val sagaStep = findSagaStep(stepHistory, currentStep)
        sagaStep.state.successNext()
    }

    fun cancelStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ) {
        val sagaStep = findSagaStep(stepHistory, currentStep)
        sagaStep.state.failedNext()
    }

    private fun findSagaStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ): SagaStep {
        return stepHistory.find { it.state.name == currentStep }
            ?: throw IllegalStateException("${currentStep}인 SagaStep이 없습니다")
    }
}