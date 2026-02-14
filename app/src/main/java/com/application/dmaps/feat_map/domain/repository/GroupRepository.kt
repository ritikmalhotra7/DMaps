package com.application.dmaps.feat_map.domain.repository

import com.application.dmaps.feat_core.data.dtos.ResponseWrapperDto
import com.application.dmaps.feat_core.utils.error.ResultError
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.GroupUser
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.utils.GroupError

interface GroupRepository {
    suspend fun createGroup():ResultState<ResponseWrapperDto<Group>,ResultError>
    suspend fun getGroup(groupId:String):ResultState<Group,ResultError>
    suspend fun joinGroup(groupCode:String):ResultState<Group,ResultError>
    suspend fun updateDestination(groupId:String, destination:Location):ResultState<Unit?,ResultError>
    suspend fun removeUser(groupId:String, id:String):ResultState<Unit?, ResultError>
    suspend fun removeCurrentUser(groupId:String):ResultState<Unit?, ResultError>
    suspend fun closeGroup(groupId:String):ResultState<Unit?, ResultError>
}