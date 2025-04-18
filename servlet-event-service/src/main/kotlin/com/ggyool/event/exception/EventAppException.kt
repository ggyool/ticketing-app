package com.ggyool.event.exception

import org.springframework.http.HttpStatus

class EventAppException(
    val httpStatus: HttpStatus,
    val reason: String,
) : RuntimeException(reason)