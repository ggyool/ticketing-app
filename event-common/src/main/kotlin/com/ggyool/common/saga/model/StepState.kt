package com.ggyool.common.saga.model

enum class StepState(
    private var succeedNext: StepState? = null,
    private var cancelNext: StepState? = null,
) {
    STARTED,
    SUCCEEDED,
    COMPENSATING,
    COMPENSATED,
    FAILED;

    fun successNext() = this.succeedNext!!

    fun cancelNext() = this.cancelNext!!

    companion object {
        init {
            STARTED.succeedNext = SUCCEEDED
            STARTED.cancelNext = FAILED

            SUCCEEDED.succeedNext = SUCCEEDED
            SUCCEEDED.cancelNext = COMPENSATING

            COMPENSATING.succeedNext = null
            COMPENSATING.cancelNext = COMPENSATED

            COMPENSATED.succeedNext = null
            COMPENSATED.cancelNext = null

            FAILED.succeedNext = null
            FAILED.cancelNext = null
        }
    }
}