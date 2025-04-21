package com.ggyool.ticketing.controller

import com.ggyool.ticketing.application.usecase.ReserveTicketUsecase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/ticketing")
@RestController
class TicketingController(
    private val reserveTicketUsecase: ReserveTicketUsecase
) {

    @PostMapping("/reserve")
    fun reserve(
        @RequestBody input: ReserveTicketUsecase.ReserveTicketInput
    ) = reserveTicketUsecase.reserveTicket(input)
}