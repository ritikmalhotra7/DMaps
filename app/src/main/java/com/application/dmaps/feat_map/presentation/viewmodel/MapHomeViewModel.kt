package com.application.dmaps.feat_map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_core.utils.snackbar.SnackbarController
import com.application.dmaps.feat_core.utils.snackbar.SnackbarEvent
import com.application.dmaps.feat_map.data.dto.group.Group
import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.data.dto.user.User
import com.application.dmaps.feat_profile.domain.usecase.UcGetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface MapIncomingEvent{
    data object CreateGroup:MapIncomingEvent
    data class JoinGroup(val groupId:String):MapIncomingEvent
    data class UpdateDestination(val groupId:String, val destination:Location):MapIncomingEvent
    data class RemoveUserFromGroup(val username:String):MapIncomingEvent
    data class CloseGroup(val groupId:String):MapIncomingEvent
}

@HiltViewModel
class MapHomeViewModel @Inject constructor(
    private val ucGetCurrentUser: UcGetCurrentUser
):ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _groupData = MutableStateFlow<Group?>(null)
    val groupData = _groupData.asStateFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun onEvent(event: MapIncomingEvent) {
        when(event){
            is MapIncomingEvent.CreateGroup -> {}
            is MapIncomingEvent.JoinGroup -> {}
            is MapIncomingEvent.UpdateDestination -> {}
            is MapIncomingEvent.RemoveUserFromGroup -> {}
            is MapIncomingEvent.CloseGroup -> {}
        }
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO){
        _isLoading.emit(true)
        ucGetCurrentUser().let{state->
            when(state){
                is ResultState.Success -> {
                    _user.update{ state.data }
                    _isLoading.emit(false)
                }
                is ResultState.Error -> {
                    _isLoading.emit(false)
                    SnackbarController.sendEvent(SnackbarEvent(message = state.error.message))
                }
            }
        }
    }


}