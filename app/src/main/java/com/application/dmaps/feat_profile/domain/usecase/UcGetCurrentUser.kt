package com.application.dmaps.feat_profile.domain.usecase

import com.application.dmaps.feat_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UcGetCurrentUser @Inject constructor(private val repo:ProfileRepository) {
    suspend operator fun invoke() = repo.getCurrentUser()
}