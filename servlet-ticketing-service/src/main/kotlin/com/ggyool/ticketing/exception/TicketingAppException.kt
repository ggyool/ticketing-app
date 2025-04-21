package com.ggyool.ticketing.exception

import org.springframework.http.HttpStatus

class TicketingAppException(
    val httpStatus: HttpStatus,
    val reason: String,
) : RuntimeException(reason)