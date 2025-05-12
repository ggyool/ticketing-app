package com.ggyool.common.saga.model

enum class StepState(
    private var succeedNext: StepState? = null,
    private var failedNext: StepState? = null,
) {
    STARTED,
    SUCCEEDED,
    COMPENSATING,
    COMPENSATED,
    FAILED;

    fun successNext()  = this.succeedNext!!

    fun failedNext() = this.failedNext!!

    companion object {
        init {
            STARTED.succeedNext = SUCCEEDED
            STARTED.failedNext = FAILED

            SUCCEEDED.succeedNext = SUCCEEDED
            SUCCEEDED.failedNext = COMPENSATING

            COMPENSATING.succeedNext = COMPENSATED
            COMPENSATING.failedNext = COMPENSATING

            COMPENSATED.succeedNext = COMPENSATED
            COMPENSATED.failedNext = COMPENSATED

            FAILED.succeedNext = FAILED
            FAILED.failedNext = FAILED
        }
    }
}