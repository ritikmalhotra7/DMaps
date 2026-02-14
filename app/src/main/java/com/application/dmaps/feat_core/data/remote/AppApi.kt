package com.application.dmaps.feat_core.data.remote

import com.application.dmaps.feat_auth.data.dtos.login.LoginData
import com.application.dmaps.feat_auth.data.dtos.login.LoginRequestDto
import com.application.dmaps.feat_auth.data.dtos.signup.SignUpRequest
import com.application.dmaps.feat_core.data.dtos.ResponseWrapperDto
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.domain.model.RemoveUserRequest
import com.application.dmaps.feat_profile.data.dto.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

const val BASE_URL = "http://10.0.2.2:8080/"
const val SOCKET_URL = "ws://10.0.2.2:8080"
//const val BASE_URL = "http://192.168.1.5:8080/"
//const val SOCKET_URL = "ws://192.168.1.5:8080"
interface AppApi {

    @POST("signup")
    suspend fun signUp(@Body request: SignUpRequest):Response<ResponseWrapperDto<Unit?>>

    @POST("signin")
    suspend fun login(@Body request: LoginRequestDto): Response<ResponseWrapperDto<LoginData>>

    @GET("currentUser")
    suspend fun getCurrentUser():Response<ResponseWrapperDto<User>>

    @POST("create-group")
    suspend fun createGroup():Response<ResponseWrapperDto<Group>>

    @GET("group/{groupId}")
    suspend fun getGroup(@Path("groupId") groupId: String): Response<ResponseWrapperDto<Group>>

    @PUT("join-group/{groupCode}")
    suspend fun joinGroup(@Path("groupCode") groupCode:String):Response<ResponseWrapperDto<Group>>

    @PUT("update-destination/{groupId}")
    suspend fun updateDestination(@Path("groupId") groupId:String,@Body destination:Location):Response<ResponseWrapperDto<Unit?>>

    @PUT("remove-users/{groupId}")
    suspend fun removeUser(@Path("groupId") groupId:String,@Body request: RemoveUserRequest):Response<ResponseWrapperDto<Unit?>>

    @PUT("remove-user/{groupId}")
    suspend fun removeCurrentUser(@Path("groupId") groupId:String):Response<ResponseWrapperDto<Unit?>>

    @DELETE("close/{groupId}")
    suspend fun closeGroup(@Path("groupId") groupId:String):Response<ResponseWrapperDto<Unit?>>
}