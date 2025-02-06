package com.application.dmaps.feat_core.utils.result

import com.application.dmaps.feat_core.utils.error.ResultError


sealed interface ResultState<out D,out E: ResultError> {
    data class Success<out D, out E: ResultError>(val data:D): ResultState<D, E>
    data class Error<out D, out E : ResultError>(val error:E): ResultState<D, E>
}