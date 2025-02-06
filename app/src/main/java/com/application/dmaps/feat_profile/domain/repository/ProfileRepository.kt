package com.application.dmaps.feat_profile.domain.repository

import com.application.dmaps.feat_core.data.dtos.ResponseWrapperDto
import com.application.dmaps.feat_core.utils.result.ResultState
import com.application.dmaps.feat_map.data.dto.user.User
import com.application.dmaps.feat_profile.utils.ProfileError

interface ProfileRepository {
    suspend fun getCurrentUser():ResultState<User,ProfileError>
}