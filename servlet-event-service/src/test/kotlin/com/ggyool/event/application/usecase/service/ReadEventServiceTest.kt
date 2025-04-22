package com.ggyool.event.application.usecase.service

import com.ggyool.event.exception.EventAppException
import com.ggyool.event.repository.EventJpaRepository
import com.ggyool.event.repository.entity.EventEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class ReadEventServiceTest {

    @MockK
    private lateinit var eventRepository: EventJpaRepository

    @InjectMockKs
    private lateinit var readEventService: ReadEventService

    @DisplayName("이벤트 조회에 성공한다")
    @Test
    fun readEventSuccessfully() {
        // given
        val id = 1L
        val savedEntity = EventEntity(
            id = id,
            name = "아이유 콘서트",
            ticketQuantity = 50000L,
            version = 0L,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        every { eventRepository.findByIdOrNull(id) } returns savedEntity

        //when
        val output = readEventService.readEvent(id)

        // then
        verify(exactly = 1) { eventRepository.findByIdOrNull(1L) }
    }

    @DisplayName("이벤트가 존재하지 않는 경우 에러가 발생한다")
    @Test
    fun readNonexistentEvent() {
        // given
        val id = 1L

        every { eventRepository.findByIdOrNull(id) } returns null

        // when, then
        assertThatThrownBy {
            readEventService.readEvent(id)
        }
            .isInstanceOf(EventAppException::class.java)
            .hasMessageContaining("id가 ${id}인 event가 존재하지 않습니다")
            .extracting("httpStatus")
            .isEqualTo(HttpStatus.NOT_FOUND)
    }
}