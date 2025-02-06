package com.application.dmaps.feat_auth.data.repository

import com.application.dmaps.feat_core.data.remote.AppApi
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_auth.data.dtos.login.LoginRequestDto
import com.application.dmaps.feat_auth.data.dtos.login.LoginResponseDto
import com.application.dmaps.feat_auth.domian.repository.AuthRepository
import com.application.dmaps.feat_auth.utils.LoginError
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val api:AppApi): AuthRepository {
    override suspend fun login(request: LoginRequestDto): ResultState<LoginResponseDto, LoginError> {
        return try{
            val data = api.login(request)
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!)
                }else{
                    ResultState.Error(LoginError.CustomError(data.body()!!.message?:""))
                }
            }else{
                ResultState.Error(LoginError.CustomError(data.body()!!.message?:""))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(LoginError.CustomError(e.message.toString()))
        }
    }
}