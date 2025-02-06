package com.application.dmaps.feat_map.domain.usecases

import com.application.dmaps.feat_map.domain.repository.GroupRepository
import javax.inject.Inject

class CloseGroupUsecase @Inject constructor(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId:String) = repo.closeGroup(groupId)
}