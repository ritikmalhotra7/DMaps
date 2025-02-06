package com.application.dmaps.feat_profile.utils

import com.application.dmaps.feat_core.utils.error.ResultError

sealed class ProfileError(override val message: String): ResultError {
    data object UserNotFound:ProfileError("User Not found!")
    data class CustomError(val e:String):ProfileError(e)
}