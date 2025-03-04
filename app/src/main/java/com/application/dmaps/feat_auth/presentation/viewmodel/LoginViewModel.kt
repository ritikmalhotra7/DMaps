package com.application.dmaps.feat_auth.presentation.viewmodel

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.dmaps.feat_auth.data.dtos.login.LoginRequestDto
import com.application.dmaps.feat_auth.domian.usecases.LoginUser
import com.application.dmaps.feat_auth.utils.AuthConstants.AUTHENTICATION_TOKEN
import com.application.dmaps.feat_core.domain.usecases.datastore.PutPreferenceUsecase
import com.application.dmaps.feat_core.utils.error.DataStoreError
import com.application.dmaps.feat_core.utils.result.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null
)

sealed interface LoginScreenIncomingEvent {
    data class OnUsernameChanged(val username: String) : LoginScreenIncomingEvent
    data class OnPasswordChanged(val password: String) : LoginScreenIncomingEvent
    data class OnLoginClicked(val username:String, val password:String) : LoginScreenIncomingEvent
}

sealed interface LoginScreenOutgoingEvent {
    data object LoginSuccess : LoginScreenOutgoingEvent
    data class ShowSnackBar(val message: String) : LoginScreenOutgoingEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUsecase: LoginUser,
    private val putPreferenceUsecase: PutPreferenceUsecase
) : ViewModel() {
    private val viewModelScopeContext =
        Dispatchers.IO + CoroutineExceptionHandler { coroutineContext, throwable -> throwable.printStackTrace() }

    private val _loginState = MutableStateFlow(LoginScreenState())
    val loginState = _loginState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginScreenOutgoingEvent>()
    val eventFLow get() = _eventFlow


    fun onEvent(event:LoginScreenIncomingEvent){
        when(event){
            is LoginScreenIncomingEvent.OnUsernameChanged -> _loginState.value = _loginState.value.copy(username = event.username)
            is LoginScreenIncomingEvent.OnPasswordChanged -> _loginState.value = _loginState.value.copy(password = event.password)
            is LoginScreenIncomingEvent.OnLoginClicked -> onLoginClicked(event.username,event.password)
            else -> {}
        }
    }

    private fun onLoginClicked(username: String, password: String) =
        viewModelScope.launch(viewModelScopeContext) {
            loginUsecase(LoginRequestDto(username = username, password = password)).let { state ->
                when (state) {
                    is ResultState.Success -> {
                        if (putPreferenceUsecase(
                                stringPreferencesKey(AUTHENTICATION_TOKEN),
                                state.data.token
                            )
                        ) _eventFlow.emit(LoginScreenOutgoingEvent.LoginSuccess)
                        else _eventFlow.emit(LoginScreenOutgoingEvent.ShowSnackBar(DataStoreError.UnexpectedError.message))
                    }

                    is ResultState.Error -> {
                        _eventFlow.emit(LoginScreenOutgoingEvent.ShowSnackBar(state.error.message))
                    }
                }
            }
        }
}