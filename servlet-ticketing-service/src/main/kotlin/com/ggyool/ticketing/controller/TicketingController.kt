package com.ggyool.ticketing.controller

import com.ggyool.ticketing.application.usecase.ReserveTicketingUsecase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/ticketing")
@RestController
class TicketingController(
    private val reserveTicketingUsecase: ReserveTicketingUsecase
) {

    @PostMapping("/reserve")
    fun reserve(
        @RequestBody input: ReserveTicketingUsecase.ReserveTicketInput
    ) = reserveTicketingUsecase.reserveTicketing(input)
}