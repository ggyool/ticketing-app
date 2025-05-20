package com.ggyool.ticketing.controller

import com.ggyool.ticketing.application.usecase.ProcessTicketingUsecase
import com.ggyool.ticketing.application.usecase.QueryProcessTicketingUsecase
import com.ggyool.ticketing.application.usecase.ReserveTicketingUsecase
import org.springframework.web.bind.annotation.*

@RequestMapping("/ticketing")
@RestController
class TicketingController(
    private val reserveTicketingUsecase: ReserveTicketingUsecase,
    private val processTicketingUsecase: ProcessTicketingUsecase,
    private val queryProcessTicketingUsecase: QueryProcessTicketingUsecase,
) {

    @PostMapping("/reserve")
    fun reserve(
        @RequestBody input: ReserveTicketingUsecase.ReserveTicketingInput
    ) = reserveTicketingUsecase.reserveTicketing(input)

    @PostMapping("/process")
    fun process(
        @RequestBody input: ProcessTicketingUsecase.ProcessTicketingInput
    ) = processTicketingUsecase.processTicketing(input)

    @GetMapping("/process/{ticketId}")
    fun queryProcess(
        @PathVariable("ticketId") ticketId: String
    ) = queryProcessTicketingUsecase.queryProcessTicketing(ticketId)
}