package com.ggyool.event.application.usecase.service

import com.ggyool.event.application.usecase.ModifyEventUsecase
import com.ggyool.event.exception.EventAppException
import com.ggyool.event.repository.EventJpaRepository
import com.ggyool.event.repository.entity.EventEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
class ModifyEventServiceTest {

    @MockK
    private lateinit var eventRepository: EventJpaRepository

    @InjectMockKs
    private lateinit var modifyEventService: ModifyEventService

    @DisplayName("이벤트 수정에 성공한다")
    @Test
    fun modifyEventSuccessfully() {
        // given
        val id = 1L
        val savedEntity = EventEntity(
            id = id,
            name = "아이유 콘서트",
            ticketQuantity = 50000L
        )

        every { eventRepository.findByIdOrNull(id) } returns savedEntity

        //when
        modifyEventService.modifyEvent(
            id,
            ModifyEventUsecase.ModifyEventInput("IU Concert", 10000L)
        )

        // then
        assertThat(savedEntity.name).isEqualTo("IU Concert")
        assertThat(savedEntity.ticketQuantity).isEqualTo(10000L)
    }

    @DisplayName("이벤트가 존재하지 않는 경우 에러가 발생한다")
    @Test
    fun modifyNonexistentEvent() {
        // given
        val id = 1L

        every { eventRepository.findByIdOrNull(id) } returns null

        // when, then
        assertThatThrownBy {
            modifyEventService.modifyEvent(
                id,
                ModifyEventUsecase.ModifyEventInput("IU Concert", 10000L)
            )
        }
            .isInstanceOf(EventAppException::class.java)
            .hasMessageContaining("id가 ${id}인 event가 존재하지 않습니다")
            .extracting("httpStatus")
            .isEqualTo(HttpStatus.NOT_FOUND)
    }
}