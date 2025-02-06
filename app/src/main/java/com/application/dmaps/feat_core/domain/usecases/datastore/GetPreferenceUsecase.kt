package com.application.dmaps.feat_core.domain.usecases.datastore

import androidx.datastore.preferences.core.Preferences
import com.application.dmaps.feat_core.domain.repository.CoreRepository
import javax.inject.Inject

class GetPreferenceUsecase @Inject constructor(private val repo: CoreRepository) {
    suspend operator fun <T> invoke(
        key: Preferences.Key<T>,
        defaultValue: T
    ): T = repo.getFirstPreference(key, defaultValue)
}