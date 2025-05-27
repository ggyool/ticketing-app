package com.ggyool.ticketing.application.worker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggyool.ticketing.application.usecase.MovePaymentCompletedQueueUsecase
import com.ggyool.ticketing.application.usecase.RegisterTicketUsecase
import com.ggyool.ticketing.helper.consumeWithDlt
import com.ggyool.ticketing.repository.entity.TicketStatus
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class PaymentDomainEventConsumer(
    private val movePaymentCompletedQueueUsecase: MovePaymentCompletedQueueUsecase,
    private val registerTicketUsecase: RegisterTicketUsecase,
    private val objectMapper: ObjectMapper,
) {

    // TODO
    // 만들다가 생각해보니 도메인 이벤트 consume 실패할 때 재발급 유도하는게 말이 안되는 구조라는 걸 깨달음 (여러 곳에서 구독하고 있기 때문에)
    // 구현하던거 완성하고 재시도가 가능한 경우와 아닌 경우 구분해야함
    // 지금 생각은 커밋하지 않고 lag을 조정해서 재처리 하도록 유도하는 방향 생각
    @KafkaListener(
        topics = ["domain.payment"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "payment-domain-event-group",
        concurrency = "2"
    )
    fun listenPaymentDomainEvent(
        record: ConsumerRecord<String, String>,
        acknowledge: Acknowledgment
    ) = consumeWithDlt(record, acknowledge) {
        val event = objectMapper.readValue<PaymentDomainEvent>(record.value())
        val eventType = event.eventType

        if (eventType == "update" && event.payload.status.succeed()) {
            val moved = movePaymentCompletedQueueUsecase.movePaymentCompletedQueue(
                MovePaymentCompletedQueueUsecase.MovePaymentCompletedQueueInput(
                    eventId = event.payload.eventId,
                    userId = event.payload.userId,
                    ticketId = event.payload.ticketId,
                )
            )
            registerTicketUsecase.registerTicket(
                RegisterTicketUsecase.RegisterTicketInput(
                    ticketId = event.payload.ticketId,
                    eventId = event.payload.eventId,
                    userId = event.payload.userId,
                    status = if (moved) TicketStatus.RESERVED else TicketStatus.CANCELLED
                )
            )
            // TODO
            // 실패시 결제 취소 이벤트 발행
        }
    }
}