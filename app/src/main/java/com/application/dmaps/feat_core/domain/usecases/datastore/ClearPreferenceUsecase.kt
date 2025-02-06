package com.application.dmaps.feat_core.domain.usecases.datastore

import com.application.dmaps.feat_core.domain.repository.CoreRepository
import javax.inject.Inject

class ClearPreferenceUsecase @Inject constructor(private val repo: CoreRepository) {
    suspend operator fun invoke() = repo.clearPreferences()
}