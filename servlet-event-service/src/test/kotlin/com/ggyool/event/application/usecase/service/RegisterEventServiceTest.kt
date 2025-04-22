package com.ggyool.event.application.usecase.service

import com.ggyool.event.application.usecase.RegisterEventUsecase
import com.ggyool.event.repository.EventJpaRepository
import com.ggyool.event.repository.entity.EventEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RegisterEventServiceTest {

    @MockK
    private lateinit var eventRepository: EventJpaRepository

    @InjectMockKs
    private lateinit var registerEventService: RegisterEventService

    @DisplayName("이벤트 등록에 성공한다")
    @Test
    fun registerEventSuccessfully() {
        // given
        val name = "아이유 콘서트"
        val ticketQuantity = 50000L

        every { eventRepository.save(any()) } returns EventEntity(
            id = 1,
            name = name,
            ticketQuantity = ticketQuantity
        )

        // when
        val id = registerEventService.registerEvent(
            RegisterEventUsecase.RegisterEventInput(
                name, ticketQuantity
            )
        )

        // then
        verify(exactly = 1) {
            eventRepository.save(match {
                it.name == name && it.ticketQuantity == 50000L
            })
        }
        assertThat(id).isEqualTo(1L)
    }
}