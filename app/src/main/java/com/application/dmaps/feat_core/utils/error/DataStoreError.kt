package com.application.dmaps.feat_core.utils.error

sealed class DataStoreError(override val message: String): ResultError {
    data object UnexpectedError : DataStoreError("Unable to save data")
}