package com.application.dmaps.feat_map.utils

import com.application.dmaps.feat_core.utils.error.ResultError

sealed class GroupError(override val message:String):ResultError{
    data object GroupNotFound:GroupError("Group Not Found")
    data object InfoNotUpdated:GroupError("Info Not Updated Due to Technical Error!")
}