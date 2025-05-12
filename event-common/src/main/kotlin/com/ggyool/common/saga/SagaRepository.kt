package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import java.util.*

interface SagaRepository<T : SagaContext> {

    fun findByIdOrNull(id: UUID): T?

    fun save(sagaContext: T): T
}