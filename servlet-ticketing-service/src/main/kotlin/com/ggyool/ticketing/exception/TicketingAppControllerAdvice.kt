package com.ggyool.ticketing.exception

import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class TicketingAppControllerAdvice {

    @ExceptionHandler
    fun handleEventAppException(ex: TicketingAppException) =
        ErrorResponse.builder(ex, ex.httpStatus, ex.reason).build()
}