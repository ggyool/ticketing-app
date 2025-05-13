package com.ggyool.ticketing.application.saga

import com.ggyool.common.saga.model.SagaContextFactory
import com.ggyool.common.saga.model.SagaState
import com.ggyool.common.saga.model.SagaStep
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class SagaContextEntityFactory : SagaContextFactory<SagaContextEntity> {

    override fun createWithAllArgs(
        id: UUID,
        version: Long,
        sagaType: String,
        payload: String?,
        currentStep: String,
        stepHistory: List<SagaStep>,
        sagaState: SagaState
    ): SagaContextEntity {
        return SagaContextEntity(
            id = id,
            version = version,
            sagaType = sagaType,
            payload = payload,
            currentStep = currentStep,
            stepHistory = stepHistory,
            sagaState = sagaState
        )
    }
}