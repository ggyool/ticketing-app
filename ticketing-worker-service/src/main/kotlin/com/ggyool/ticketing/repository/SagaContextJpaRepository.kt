package com.ggyool.ticketing.repository

import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SagaContextJpaRepository : CrudRepository<SagaContextEntity, UUID>