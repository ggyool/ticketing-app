package com.ggyool.common.saga.model

import java.util.*

data class SagaResponse(
    val sagaId: UUID,
    val payload: String
)