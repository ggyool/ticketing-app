package com.ggyool.event.exception

import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ReservationAppControllerAdvice {

    @ExceptionHandler
    fun handleReservationAppException(ex: ReservationAppException) =
        ErrorResponse.builder(ex, ex.httpStatus, ex.reason).build()
}