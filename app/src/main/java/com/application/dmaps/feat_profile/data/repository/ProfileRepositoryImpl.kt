package com.application.dmaps.feat_profile.data.repository

import com.application.dmaps.feat_core.data.remote.AppApi
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_profile.data.dto.user.User
import com.application.dmaps.feat_profile.domain.repository.ProfileRepository
import com.application.dmaps.feat_profile.utils.ProfileError
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(private val api:AppApi):ProfileRepository {
    override suspend fun getCurrentUser(): ResultState<User,ProfileError> {
        return try{
            val data = api.getCurrentUser()
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data!!)
                }else{
                    ResultState.Error(ProfileError.CustomError(data.body()!!.message?:""))
                }
            }else{
                ResultState.Error(ProfileError.CustomError(data.body()!!.message?:""))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(ProfileError.CustomError(e.message.toString()))
        }
    }
}