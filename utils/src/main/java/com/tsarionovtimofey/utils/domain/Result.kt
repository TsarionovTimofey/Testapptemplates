package com.tsarionovtimofey.utils.domain


sealed class Result<T : Any> {

    class Success<T : Any>(
        val value: T
    ) : Result<T>()

    class Error<T : Any>(
        val exception: Throwable
    ) : Result<T>()

    class Pending<T : Any> : Result<T>()

}
