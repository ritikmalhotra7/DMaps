package com.application.dmaps.feat_auth.utils

import com.application.dmaps.feat_core.utils.error.ResultError

sealed class LoginError(override val message: String): ResultError {
    data object EmptyUsername : LoginError("Username cannot be empty.")
    data object EmptyPassword : LoginError("Password cannot be empty.")
    data object PasswordTooShort : LoginError("Password must have 8 characters.")
    data class CustomError(val e:String):LoginError(e)
}