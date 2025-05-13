package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaResponse

interface Saga<T : SagaContext> {

    fun start(initialContext: T): T

    fun process(sagaResponse: SagaResponse): T

    fun firstStepName(): String
}