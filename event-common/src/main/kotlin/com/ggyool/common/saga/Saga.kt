package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaPayload
import com.ggyool.common.saga.model.SagaResponse

interface Saga<T : SagaContext> {

    fun start(sagaType: String, payload: SagaPayload): T

    fun onResponse(sagaResponse: SagaResponse): T
}