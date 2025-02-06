package com.application.dmaps.feat_auth.domian.repository

import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_auth.data.dtos.login.LoginRequestDto
import com.application.dmaps.feat_auth.data.dtos.login.LoginResponseDto
import com.application.dmaps.feat_auth.utils.LoginError

interface AuthRepository{
    suspend fun login(request:LoginRequestDto): ResultState<LoginResponseDto, LoginError>
}