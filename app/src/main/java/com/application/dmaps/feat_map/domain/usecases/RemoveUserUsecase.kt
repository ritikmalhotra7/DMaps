package com.application.dmaps.feat_map.domain.usecases

import com.application.dmaps.feat_map.domain.repository.GroupRepository
import javax.inject.Inject

class RemoveUserUsecase@Inject constructor(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId:String,id:String) = repo.removeUser(groupId,id)
}