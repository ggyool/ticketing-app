package com.ggyool.common.saga.model

import java.util.*

data class SagaRequest(
    val sagaId: UUID,
    val payload: String,
)