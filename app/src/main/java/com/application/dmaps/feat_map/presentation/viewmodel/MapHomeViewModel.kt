package com.application.dmaps.feat_map.presentation.viewmodel

import android.Manifest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.dmaps.feat_core.utils.Event
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_core.utils.snackbar.SnackbarController
import com.application.dmaps.feat_core.utils.snackbar.SnackbarEvent
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.domain.usecases.CloseGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.CreateGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.JoinGroupUsecase
import com.application.dmaps.feat_map.domain.usecases.RemoveCurrentUserUsecase
import com.application.dmaps.feat_map.domain.usecases.UpdateDestinationUsecase
import com.application.dmaps.feat_profile.data.dto.user.User
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class   MapHomeViewModel @Inject constructor(
    private val ucGetCurrentUser: UcGetCurrentUser,
    private val ucCreateGroup: CreateGroupUsecase,
    private val ucJoinGroup: JoinGroupUsecase,
    private val ucRemoveCurrentUser: RemoveCurrentUserUsecase,
    private val ucUpdateDestination: UpdateDestinationUsecase,
    private val ucCloseGroup: CloseGroupUsecase
) : ViewModel() {

    private val viewModelScopeContext =
        Dispatchers.IO + CoroutineExceptionHandler { coroutineContext, throwable ->
            viewModelScope.launch {
                _isLoading.emit(false)
            }
            throwable.printStackTrace()
        }

    val permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.onStart {
        _user.value ?: kotlin.run {
            getCurrentUser()
        }
    }.stateIn(
        scope = viewModelScope.plus(viewModelScopeContext),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val groupId = user.filterNotNull().mapLatest {
        it.groupId
    }.stateIn(
        scope = viewModelScope.plus(viewModelScopeContext),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val mGroup = MutableStateFlow<Group?>(null)
    val group = mGroup.asStateFlow()

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

    private fun getCurrentUser() = viewModelScope.launch(viewModelScopeContext) {
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

    //updating group via socket
    fun updateGroup(groupData: Group?) {
        mGroup.update { groupData }
    }

    fun handleGroupCreation() = viewModelScope.launch(viewModelScopeContext) {
        ucCreateGroup().let { state ->
            when (state) {
                is ResultState.Success -> {
                    state.data.message?.let{message->
                        SnackbarController.sendEvent(SnackbarEvent(message = message))
                    }
                    mGroup.update { state.data.data }
                }

                is ResultState.Error -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }

    fun handleGroupCodeSubmission(groupCode: String) =
        viewModelScope.launch(viewModelScopeContext) {
            ucJoinGroup(groupCode).let { state ->
                when (state) {
                    is ResultState.Success -> {
                        mGroup.update{ state.data }
                    }

                    is ResultState.Error -> {
                        SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                    }
                }
            }
        }

    fun onLeaveGroupClicked() = viewModelScope.launch(viewModelScopeContext) {

    }
}