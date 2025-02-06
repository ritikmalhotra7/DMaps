package com.application.dmaps.feat_core.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface CoreRepository {
    //Data Store Functions
    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue:T): Flow<T>
    suspend fun <T> getFirstPreference(key:Preferences.Key<T>,defaultValue:T):T
    suspend fun <T> putPreference(key: Preferences.Key<T>, value:T):Boolean
    suspend fun <T> removePreference(key: Preferences.Key<T>):Boolean
    suspend fun clearPreferences():Boolean


}