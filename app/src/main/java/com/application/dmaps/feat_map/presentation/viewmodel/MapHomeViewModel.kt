package com.application.dmaps.feat_map.presentation.viewmodel

import android.Manifest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.dmaps.feat_core.utils.Event
import com.application.dmaps.feat_core.utils.logd
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_core.utils.snackbar.SnackbarController
import com.application.dmaps.feat_core.utils.snackbar.SnackbarEvent
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_profile.data.dto.user.User
import com.application.dmaps.feat_map.domain.usecases.CreateGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.GetGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.JoinGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.RemoveCurrentUserUsecase
import com.application.dmaps.feat_map.domain.usecases.UpdateDestinationUsecase
import com.application.dmaps.feat_profile.domain.usecase.UcGetCurrentUser
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject


sealed interface MapScreenEvent:Event {
    data class OnCreateGroup(val group:Group) : MapScreenEvent
    data object OnGroupJoined : MapScreenEvent
    data object OnGroupClosed:MapScreenEvent
}

@HiltViewModel
class MapHomeViewModel @Inject constructor(
    private val ucGetCurrentUser: UcGetCurrentUser,
    private val ucGetGroupInfo: GetGroupUsecase,
    private val ucCreateGroupUsecase: CreateGroupUsecase,
    private val ucJoinGroupUsecase: JoinGroupUsecase,
    private val ucRemoveCurrentUserUsecase: RemoveCurrentUserUsecase,
    private val ucUpdateDestinationUsecase: UpdateDestinationUsecase
) : ViewModel() {

    private val viewModelScopeContext = Dispatchers.IO + CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    val permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.onStart {
        _user.value?: kotlin.run {
            getCurrentUser()
        }
    }.stateIn(
        scope = viewModelScope.plus(viewModelScopeContext),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _groupData = MutableStateFlow<Group?>(null)
    val groupData = _groupData.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.filterNotNull().map {
        LatLng(it.latitude, it.longitude)
    }.stateIn(
        scope = viewModelScope.plus(viewModelScopeContext),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = LatLng(28.7041, 77.1025)
    )

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    val permissionsNotAccepted = mutableStateListOf<String>()

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        _isLoading.emit(true)
        ucGetCurrentUser().let { state ->
            when (state) {
                is ResultState.Success -> {
                    _user.update { state.data }
                    _isLoading.emit(false)
                }

                is ResultState.Error -> {
                    _isLoading.emit(false)
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted && !permissionsNotAccepted.contains(permission)) {
            permissionsNotAccepted.add(permission)
        }
    }

    fun onDismissPermissionDialog() {
        permissionsNotAccepted.remove(permissionsNotAccepted.first())
    }

    fun updateGroup(groupData: Group?) {
        groupData?.logd("group-data")
        _groupData.update {
            groupData
        }
    }
    fun onClickCreateGroup() = viewModelScope.launch(viewModelScopeContext){
        ucCreateGroupUsecase().let{state->
            when(state){
                is ResultState.Success -> {
                    "created".logd()
                    _eventFlow.emit(MapScreenEvent.OnCreateGroup(state.data))
                }
                is ResultState.Error -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }

    fun onJoinClicked(groupCode:String) = viewModelScope.launch(viewModelScopeContext){
        ucJoinGroupUsecase(groupCode).let{state->
            when(state){
                is ResultState.Success -> {
                    _eventFlow.emit(MapScreenEvent.OnGroupJoined)
                }
                is ResultState.Error -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }

    fun removeCurrentUser() = viewModelScope.launch(viewModelScopeContext){
        ucRemoveCurrentUserUsecase(groupId = groupData.value?._id?:"").let{state->
            when(state){
                is ResultState.Success -> {
                    _groupData.update { null }
                    _eventFlow.emit(MapScreenEvent.OnGroupClosed)
                }
                is ResultState.Error -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }
}