package com.ggyool.payment.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean("kafkaTemplate")
    @Primary
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            producerFactory(
                kafkaProperties.bootstrapServers
            )
        )
    }

    @Bean("deadLetterKafkaTemplate")
    fun deadLetterKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            producerFactory(
                kafkaProperties.properties["dead-letter-bootstrap-servers"]!!.split(",")
            )
        )
    }

    private fun producerFactory(bootstrapServers: List<String>): ProducerFactory<String, String> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        // 모든 복제본이 메시지를 받을 때까지 대기 (-1)
        props[ProducerConfig.ACKS_CONFIG] = "-1"
        // 중복 메시지 방지를 위한 멱등성 프로듀서 활성화
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        // 전송 실패 시 최대 3번까지 재시도
        props[ProducerConfig.RETRIES_CONFIG] = 3
        // 재시도 간격을 1초로 설정
        props[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = 1000
        // 브로커의 응답 대기 시간
        props[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = 5000
        // 메시지를 즉시 전송 (배치 처리 없음)
        props[ProducerConfig.LINGER_MS_CONFIG] = 0
        // 전체 전송 시도에 대한 타임아웃 (linger.ms + request.timeout.ms보다 커야 함)
        props[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = 7000
        return DefaultKafkaProducerFactory(props)
    }
}