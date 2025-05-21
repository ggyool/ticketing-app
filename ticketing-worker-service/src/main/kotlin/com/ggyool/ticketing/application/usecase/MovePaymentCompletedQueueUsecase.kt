package com.ggyool.ticketing.application.usecase

import java.util.*

interface MovePaymentCompletedQueueUsecase {

    fun movePaymentCompletedQueue(movePaymentCompletedQueueInput: MovePaymentCompletedQueueInput): Boolean

    data class MovePaymentCompletedQueueInput(
        val eventId: Long,
        val userId: Long,
        val ticketId: UUID,
    )
}