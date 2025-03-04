package com.application.dmaps.feat_map.data.dto.group

data class Group(
    val _id:String,
    val groupCode:String,
    var users:List<GroupUser>,
    val destination:Location? = null,
    val createdAt:String,
    val host:String
)
