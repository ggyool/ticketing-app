package com.ggyool.user.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .findAndRegisterModules()
            .registerModule(JavaTimeModule())
            .apply {
                this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
    }

}