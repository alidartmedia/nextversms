package com.dartmedia.nextversms.utils

interface VerifyListener {
    fun onSuccess()
    fun onFailed(errorMessage: String)
}