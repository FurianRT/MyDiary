package com.furianrt.mydiary.model.source.billing.entity

sealed class ConnectionState {
    object Connected : ConnectionState()
    sealed class Error(open val exception: Throwable?) : ConnectionState() {
        class Unknown(override val exception: Throwable? = null) : Error(exception)
    }
    object Disconnected : ConnectionState()
}