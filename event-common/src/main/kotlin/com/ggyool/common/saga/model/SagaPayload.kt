package com.ggyool.common.saga.model

import java.util.*

interface SagaPayload {
    val sagaId: UUID
    val referenceId: UUID
}