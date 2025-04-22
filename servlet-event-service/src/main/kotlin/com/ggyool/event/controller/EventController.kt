package com.ggyool.event.controller

import com.ggyool.event.application.usecase.ModifyEventUsecase
import com.ggyool.event.application.usecase.ReadEventUsecase
import com.ggyool.event.application.usecase.RegisterEventUsecase
import org.springframework.web.bind.annotation.*

@RequestMapping("event")
@RestController
class EventController(
    private val registerEventUsecase: RegisterEventUsecase,
    private val readEventUsecase: ReadEventUsecase,
    private val modifyEventUsecase: ModifyEventUsecase,
) {

    @PostMapping
    fun registerEvent(
        @RequestBody input: RegisterEventUsecase.RegisterEventInput
    ): Long = registerEventUsecase.registerEvent(input)

    @GetMapping("/{id}")
    fun readEvent(
        @PathVariable id: Long,
    ) = readEventUsecase.readEvent(id)

    @PutMapping("/{id}")
    fun modifyEvent(
        @PathVariable id: Long,
        @RequestBody input: ModifyEventUsecase.ModifyEventInput
    ) = modifyEventUsecase.modifyEvent(id, input)
}