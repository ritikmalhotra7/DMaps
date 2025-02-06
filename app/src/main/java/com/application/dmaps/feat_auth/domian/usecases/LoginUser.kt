package com.application.dmaps.feat_auth.domian.usecases

import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_auth.data.dtos.login.LoginRequestDto
import com.application.dmaps.feat_auth.data.dtos.login.LoginResponseDto
import com.application.dmaps.feat_auth.domian.repository.AuthRepository
import com.application.dmaps.feat_auth.utils.LoginError
import javax.inject.Inject

class LoginUser @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(request:LoginRequestDto): ResultState<LoginResponseDto, LoginError> {
        if(request.username.isBlank() || request.username.isEmpty()){
            return ResultState.Error(LoginError.EmptyUsername)
        }
        if(request.password.isBlank() || request.password.isEmpty()){
            return ResultState.Error(LoginError.EmptyPassword)
        }
        if(request.password.length < 8){
            return ResultState.Error(LoginError.PasswordTooShort)
        }
        return repo.login(request = request)
    }
}