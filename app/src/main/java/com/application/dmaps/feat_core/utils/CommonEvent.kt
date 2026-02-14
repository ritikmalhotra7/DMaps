package com.application.dmaps.feat_core.utils

import com.google.android.gms.common.internal.service.Common

interface Event
sealed interface CommonEvent:Event {
    data class IsLoading(val isLoading:Boolean):CommonEvent
}