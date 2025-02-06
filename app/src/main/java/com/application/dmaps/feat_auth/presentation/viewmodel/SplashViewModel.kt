package com.application.dmaps.feat_auth.presentation.viewmodel

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.dmaps.feat_auth.utils.AuthConstants.AUTHENTICATION_TOKEN
import com.application.dmaps.feat_core.domain.usecases.datastore.GetPreferenceUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getPreferenceUsecase: GetPreferenceUsecase
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<String?>(null)
    val isLoggedIn get() = _isLoggedIn.asStateFlow()

    init {
        updateUserLogInfo()
    }

    private fun updateUserLogInfo() = viewModelScope.launch(Dispatchers.IO) {
        val token: Deferred<String> =
            async { getPreferenceUsecase(stringPreferencesKey(AUTHENTICATION_TOKEN), "") }
        _isLoggedIn.emit(token.await())
    }
}