package com.ggyool.payment.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    @Primary
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props: MutableMap<String, Any> = HashMap()
        // Kafka 서버 연결 정보
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        // 오프셋 리셋 정책 (earliest: 처음부터, latest: 가장 최근부터)
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = kafkaProperties.consumer.autoOffsetReset
        // 키와 값의 역직렬화 방식 설정
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        // 토픽 자동 생성 비활성화
        props[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = false
        // Kafka 클라이언트의 자동 커밋 비활성화
        // true: Kafka 클라이언트가 자동으로 오프셋을 커밋
        // false: Kafka 클라이언트의 자동 커밋을 비활성화하고 Spring의 커밋 메커니즘 사용
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean(name = ["kafkaListenerContainerFactory"])
    @Primary
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        // Spring Kafka의 커밋 모드 설정
        // MANUAL: 수동 커밋 (ack.acknowledge() 호출 필요)
        // MANUAL_IMMEDIATE: 즉시 수동 커밋
        // BATCH: 배치 단위로 자동 커밋 (기본값)
        // RECORD: 레코드 단위로 자동 커밋
        // TIME: 시간 기반으로 자동 커밋
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }
}