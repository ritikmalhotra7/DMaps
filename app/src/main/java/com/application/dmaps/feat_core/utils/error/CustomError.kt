package com.application.dmaps.feat_core.utils.error

sealed class CustomError(override val message: String) :ResultError {
    data class Error(val e:String?):CustomError(e?:"Unexpected Error!")
}