package com.application.dmaps.feat_core.data.dtos

data class ResponseWrapperDto<T>(
    val success:Boolean,
    val message:String? = null,
    val data:T? = null
)
