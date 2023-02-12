package com.tsarionovtimofey.utils.domain

interface ErrorHandler {

    fun handeError(exception: Throwable)

    fun getErrorMessage(exception: Throwable): String
}