package com.ggyool.user.exception

class UserWorkerException(
    val reason: String,
) : RuntimeException(reason)