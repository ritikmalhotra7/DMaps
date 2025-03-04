package com.application.dmaps.feat_map.domain.model

import com.application.dmaps.feat_map.data.dto.group.Location

data class GroupDataRequestModel(
    val username:String,
    val location: Location
)
