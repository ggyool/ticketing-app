package com.ggyool.ticketing.application.usecase

interface ReserveTicketingUsecase {

    fun reserveTicketing(reserveTicketingInput: ReserveTicketingInput): ReserveTicketingOutput

    /**
     *  input/output 네이밍
     *  command, query, dto 등 주로 사용하는 네이밍이 있지만 모두 애매하다고 생각했음
     *  command 를 명령이라는 의미로도 사용하지만 CUD 작업이 있는 경우에 사용하는 경우도 있어서 간단하게 input/output 채택
     *  현재는 컨트롤러에서 바로 input/output 으로 요청을 받았지만 필요한 경우 api 레이어에서 request/response 사용할 수 있음
     */
    data class ReserveTicketingInput(
        val eventId: Long,
        val userId: Long,
    )

    data class ReserveTicketingOutput(
        val ticketId: String,
    )
}