package com.ggyool.ticketing.repository.entity

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaState
import com.ggyool.common.saga.model.SagaStep
import com.ggyool.ticketing.repository.converter.SagaStepListConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@EntityListeners(AuditingEntityListener::class)
@Table(name = "saga_context")
@Entity
class SagaContextEntity(
    @Id
    override val id: UUID,
    @Version
    override val version: Long,
    override val sagaType: String,
    override val payload: String,
    override val currentStep: String,
    @Convert(converter = SagaStepListConverter::class)
    override val stepHistory: List<SagaStep>,
    @Enumerated(EnumType.STRING)
    override val sagaState: SagaState,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime,
) : SagaContext
