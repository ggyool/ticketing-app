package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext

interface SagaHandler<T : SagaContext>{

    val stepName: String

    fun request(context: T): T

    fun compensate(context: T): T
}