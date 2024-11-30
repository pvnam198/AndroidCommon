package com.lingvo.billing_library.callback

interface ConnectionListener {

    fun onConnectionSuccess()

    fun onDisconnected()

    fun onConnectionFailure()

}