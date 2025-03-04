package com.application.dmaps.feat_core.utils

import android.util.Log

fun Any.logd(tag:String = "taget"){
    Log.d("taget: $tag",this.toString())
}