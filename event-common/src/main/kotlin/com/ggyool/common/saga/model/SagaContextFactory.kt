package com.ggyool.common.saga.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.util.*

interface SagaContextFactory<T : SagaContext> {

    fun createWithAllArgs(
        id: UUID,
        version: Long,
        sagaType: String,
        payload: String,
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

    fun started(sagaType: String, payload: SagaPayload, firstStep: String): T {
        return createWithAllArgs(
            id = payload.sagaId,
            version = 0L,
            sagaType = sagaType,
            payload = objectMapper.writeValueAsString(payload),
            currentStep = firstStep,
            stepHistory = listOf(SagaStep(firstStep, StepState.STARTED)),
            sagaState = SagaState.STARTED,
        )
    }

    fun completed(sagaContext: T, currentStep: String): T {
        // 전체 사가 성공 완료 처리
        val newSagaState = SagaState.COMPLETED
        // 현재 스텝 성공 처리
        val newStepHistory = succeedCurrentStep(sagaContext.stepHistory, currentStep)
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            sagaState = newSagaState,
            stepHistory = newStepHistory,
        )
    }

    fun success(sagaContext: T, currentStep: String, nextStep: String): T {
        // 현재 스텝 성공 처리
        // 새로운 스텝 넣기
        val newStepHistory = succeedCurrentStep(sagaContext.stepHistory, currentStep)
            .toMutableList().apply {
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
        val newStepHistory = cancelStep(sagaContext.stepHistory, currentStep)
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            sagaState = newSagaState,
            stepHistory = newStepHistory
        )
    }

    fun compensating(sagaContext: T, currentStep: String, previousStep: String): T {
        // 전체 사가 보상중 처리
        val newSagaState = SagaState.COMPENSATING
        // 현재 스텝 실패 또는 보상됨 처리
        // 이전 스텝 보상중 처리
        val newStepHistory = cancelStep(
            cancelStep(sagaContext.stepHistory, currentStep),
            previousStep
        )
        return copyWhenNonNullArgs(
            sagaContext = sagaContext,
            currentStep = previousStep,
            sagaState = newSagaState,
            stepHistory = newStepHistory,
        )
    }

    fun succeedCurrentStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ): List<SagaStep> {
        return stepHistory.map { step ->
            if (step.name == currentStep) {
                SagaStep(step.name, step.state.successNext())
            } else {
                step
            }
        }
    }

    fun cancelStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ): List<SagaStep> {
        return stepHistory.map { step ->
            if (step.name == currentStep) {
                SagaStep(step.name, step.state.cancelNext())
            } else {
                step
            }
        }
    }

    private fun findSagaStep(
        stepHistory: List<SagaStep>,
        currentStep: String
    ): SagaStep {
        return stepHistory.find { it.name == currentStep }
            ?: throw IllegalStateException("${currentStep}인 SagaStep이 없습니다")
    }

    companion object {
        private val objectMapper = ObjectMapper()
            .findAndRegisterModules()
            .registerModule(JavaTimeModule())
            .apply {
                this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
    }
}