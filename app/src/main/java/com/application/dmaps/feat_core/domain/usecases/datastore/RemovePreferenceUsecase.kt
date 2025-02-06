package com.application.dmaps.feat_core.domain.usecases.datastore

import com.application.dmaps.feat_core.domain.repository.CoreRepository
import javax.inject.Inject

class RemovePreferenceUsecase @Inject constructor(private val repo:CoreRepository) {
    suspend operator fun <T> invoke(key:androidx.datastore.preferences.core.Preferences.Key<T>):Boolean = repo.removePreference(key)
}