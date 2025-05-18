package com.ggyool.common.saga.model

import java.util.*

// TODO 시간 넣기
interface SagaContext {
    val id: UUID
    val version: Long
    val sagaType: String
    val payload: String
    val currentStep: String
    val stepHistory: List<SagaStep>
    val sagaState: SagaState
}