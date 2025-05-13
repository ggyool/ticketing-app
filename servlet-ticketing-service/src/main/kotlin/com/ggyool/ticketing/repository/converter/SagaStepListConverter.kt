package com.ggyool.ticketing.repository.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ggyool.common.saga.model.SagaStep
import com.ggyool.ticketing.common.ApplicationContextProvider
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SagaStepListConverter : AttributeConverter<List<SagaStep>, String> {

    override fun convertToDatabaseColumn(attribute: List<SagaStep>?): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<SagaStep> {
        return dbData?.let {
            objectMapper.readValue(it, object : TypeReference<List<SagaStep>>() {})
        } ?: emptyList()
    }
}

private val objectMapper: ObjectMapper by lazy {
    ApplicationContextProvider.getBean(ObjectMapper::class.java)
}