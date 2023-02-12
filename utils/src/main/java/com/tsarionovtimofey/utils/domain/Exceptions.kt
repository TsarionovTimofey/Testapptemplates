package com.tsarionovtimofey.utils.domain

open class AppException(
    message: String = "",
    cause: Throwable? = null
) : Exception(message, cause)

class AuthException : AppException()

class CalledNotFromUiException : AppException()

class AlreadyInProgressException : AppException()

class LoginFailedException(
    message: String,
    cause: Throwable?
) : AppException(message, cause)

class LoginCancelledException(
    cause: Throwable? = null
) : AppException(cause = cause)

class InternalException(
    cause: Throwable?
) : AppException(cause = cause)

class CantAccessFileException(
    cause: Throwable
) : AppException(cause = cause)

class ActivityNotStartedException : AppException()