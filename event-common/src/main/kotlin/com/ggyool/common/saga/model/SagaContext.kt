package com.ggyool.common.saga.model

import java.util.*

interface SagaContext {
    val id: UUID
    val version: Long
    val sagaType: String
    val payload: String?
    val currentStep: String
    val stepHistory: List<SagaStep>
    val sagaState: SagaState
}