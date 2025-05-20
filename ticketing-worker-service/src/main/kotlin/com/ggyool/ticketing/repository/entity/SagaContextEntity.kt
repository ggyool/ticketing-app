package com.ggyool.ticketing.repository.entity

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaState
import com.ggyool.common.saga.model.SagaStep
import com.ggyool.ticketing.repository.converter.SagaStepListConverter
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Table(
    name = "saga_context",
    indexes = [
        Index(name = "idx_saga_context_reference_id", columnList = "reference_id")
    ]
)
@Entity
class SagaContextEntity(
    @Id
    override val id: UUID,
    @Column(nullable = false, unique = true)
    override val referenceId: UUID,
    @Version
    override val version: Long,
    override val sagaType: String,
    @Column(length = 2000)
    override val payload: String,
    override val currentStep: String,
    @Convert(converter = SagaStepListConverter::class)
    override val stepHistory: List<SagaStep>,
    @Enumerated(EnumType.STRING)
    override val sagaState: SagaState,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : SagaContext
