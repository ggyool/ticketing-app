package com.ggyool.common.saga

import com.ggyool.common.saga.model.SagaContext
import com.ggyool.common.saga.model.SagaProceedResult
import com.ggyool.common.saga.model.SagaResponse

interface SagaHandler<T : SagaContext> {

    val stepName: String

    // 해당 스텝에 요청을 보내는 동작 정의
    fun proceed(context: T): T

    // 해당 스텝에 보상 요청을 보내는 동작 정의
    fun compensate(context: T): T

    // 보상 요청이 필요 없는 단계 보상 요청하지 않고 넘어가기 위한 메서드
    fun needsCompensate(): Boolean

    // 요청의 응답을 받았을 때 할 동작 정의 및 로직 진행 결정
    fun onProceed(context: T, sagaResponse: SagaResponse): SagaProceedResult<T>

    // 보상 요청의 응답을 받았을 떄 할 동작 정의
    fun onCompensate(context: T, sagaResponse: SagaResponse): T

    fun isFirstStep(): Boolean = false

    fun isLastStep(): Boolean = false
}