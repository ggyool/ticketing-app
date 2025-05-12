package com.ggyool.common.saga.model

data class SagaStep(
    val name: String,
    val state: StepState,
)