package com.ggyool.ticketing.exception

class TicketingWorkerException(
    val reason: String,
) : RuntimeException(reason)