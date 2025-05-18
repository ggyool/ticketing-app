package com.ggyool.common.saga.model

import com.ggyool.common.saga.model.SagaProceedResult.ProceedState.CONTINUE

data class SagaProceedResult<T : SagaContext>(
    val sagaContext: T,
    val proceedState: ProceedState
) {

    fun isContinue(): Boolean = this.proceedState == CONTINUE

    enum class ProceedState {
        CONTINUE, ROLLBACK;
    }
}

