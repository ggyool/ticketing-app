package com.ggyool.ticketing.application.saga

import com.ggyool.common.saga.SagaRepository
import com.ggyool.ticketing.repository.SagaContextJpaRepository
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class SagaContextRepository(
    private val sagaContextJpaRepository: SagaContextJpaRepository
) : SagaRepository<SagaContextEntity> {

    override fun findByIdOrNull(id: UUID): SagaContextEntity? {
        return sagaContextJpaRepository.findByIdOrNull(id)
    }

    override fun save(sagaContext: SagaContextEntity): SagaContextEntity {
        return sagaContextJpaRepository.save(sagaContext)
    }
}