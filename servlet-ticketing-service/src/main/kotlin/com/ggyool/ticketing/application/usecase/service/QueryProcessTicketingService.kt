package com.ggyool.ticketing.application.usecase.service

import com.ggyool.ticketing.application.usecase.QueryProcessTicketingUsecase
import com.ggyool.ticketing.exception.TicketingAppException
import com.ggyool.ticketing.repository.SagaContextJpaRepository
import com.ggyool.ticketing.repository.entity.SagaContextEntity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

/**
 *  ProcessTicketing 사가 호출 이후 클라이언트에서 주기적으로 완료 호출
 *  이벤트 기반으로 사가가 요청시 saga id 를 받지 못하여 reference id (여기서는 ticket id) 로 조회
 */
@Service
class QueryProcessTicketingService(
    private val sagaContextRepository: SagaContextJpaRepository
) : QueryProcessTicketingUsecase {

    override fun queryProcessTicketing(ticketId: String): SagaContextEntity {
        return sagaContextRepository.findByReferenceId(UUID.fromString(ticketId))
            ?: throw TicketingAppException(
                HttpStatus.NOT_FOUND,
                "[ticketId: ${ticketId}] ProcessTicketing 사가가 존재하지 않습니다",
            )
    }
}