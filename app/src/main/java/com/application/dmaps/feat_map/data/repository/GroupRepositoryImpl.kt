package com.application.dmaps.feat_map.data.repository

import com.application.dmaps.feat_auth.utils.LoginError
import com.application.dmaps.feat_core.data.remote.AppApi
import com.application.dmaps.feat_core.utils.error.CustomError
import com.application.dmaps.feat_core.utils.error.ResultError
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.data.dto.group.RemoveUserRequest
import com.application.dmaps.feat_map.domain.repository.GroupRepository
import com.application.dmaps.feat_map.utils.GroupError
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(private val api:AppApi):GroupRepository {
    override suspend fun createGroup(): ResultState<Unit?, ResultError> {
        return try{
            val data = api.createGroup()
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data)
                }else{
                    ResultState.Error(CustomError.Error(data.body()!!.message))
                }
            }else{
                ResultState.Error(CustomError.Error(data.body()!!.message))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(CustomError.Error(e.message.toString()))
        }
    }

    override suspend fun joinGroup(groupCode: String): ResultState<Unit?, ResultError> {
        return try{
            val data = api.joinGroup(groupCode)
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data)
                }else{
                    ResultState.Error(CustomError.Error(data.body()!!.message))
                }
            }else{
                ResultState.Error(CustomError.Error(data.body()!!.message))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(CustomError.Error(e.message.toString()))
        }
    }

    override suspend fun updateDestination(
        groupId: String,
        destination: Location
    ): ResultState<Unit?, ResultError> {
        return try{
            val data = api.updateDestination(groupId, destination)
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data)
                }else{
                    ResultState.Error(CustomError.Error(data.body()!!.message))
                }
            }else{
                ResultState.Error(CustomError.Error(data.body()!!.message))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(CustomError.Error(e.message.toString()))
        }
    }

    override suspend fun removeUser(
        groupId: String,
        username: String
    ): ResultState<Unit?, ResultError> {
        return try{
            val data = api.removeUser(groupId, RemoveUserRequest(username))
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data)
                }else{
                    ResultState.Error(CustomError.Error(data.body()!!.message))
                }
            }else{
                ResultState.Error(CustomError.Error(data.body()!!.message))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(CustomError.Error(e.message.toString()))
        }
    }

    override suspend fun closeGroup(groupId: String): ResultState<Unit?, ResultError> {
        return try{
            val data = api.closeGroup(groupId)
            if(data.isSuccessful){
                if(data.body()!!.success){
                    ResultState.Success(data.body()!!.data)
                }else{
                    ResultState.Error(CustomError.Error(data.body()!!.message))
                }
            }else{
                ResultState.Error(CustomError.Error(data.body()!!.message))
            }
        }catch(e:Exception){
            e.printStackTrace()
            ResultState.Error(CustomError.Error(e.message.toString()))
        }
    }
}