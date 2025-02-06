package com.application.dmaps.feat_map.domain.usecases

import com.application.dmaps.feat_map.data.dto.group.Location
import com.application.dmaps.feat_map.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateDestinationUsecase@Inject constructor(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId:String,destination: Location) = repo.updateDestination(groupId,destination)
}
