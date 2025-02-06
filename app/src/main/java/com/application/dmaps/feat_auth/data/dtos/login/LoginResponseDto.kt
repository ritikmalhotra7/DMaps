package com.application.dmaps.feat_auth.data.dtos.login

data class LoginResponseDto(
    val success:Boolean,
    val message:String,
    val data:LoginData
)
