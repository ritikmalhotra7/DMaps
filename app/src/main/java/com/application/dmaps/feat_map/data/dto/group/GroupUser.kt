package com.application.dmaps.feat_map.data.dto.group

data class GroupUser(
    val id:String,
    val username:String,
    val location:Location? = null,
    val isSharing:Boolean = false
)
