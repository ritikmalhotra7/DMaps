package com.application.dmaps.feat_map.domain.usecases

import com.application.dmaps.feat_map.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUsecase @Inject constructor(private val repo:GroupRepository) {
    suspend operator fun invoke() = repo.createGroup()
}