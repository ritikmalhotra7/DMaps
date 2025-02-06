package com.application.dmaps.feat_core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.application.dmaps.feat_core.data.remote.AppApi
import com.application.dmaps.feat_core.domain.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CoreRepositoryImpl @Inject constructor(private val api:AppApi, private val dataStore: DataStore<Preferences>):CoreRepository {
    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.catch{exception->
            if(exception is IOException) emit(emptyPreferences())
            else throw exception
        }.map{preferences->
            preferences[key]?:defaultValue
        }

    override suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T): T =
        dataStore.data.first()[key]?:defaultValue

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T): Boolean {
        return try{
            dataStore.edit {preferences-> preferences[key] = value }
            true
        }catch (e:Exception){
            false
        }
    }

    override suspend fun <T> removePreference(key: Preferences.Key<T>): Boolean {
        return try{
            dataStore.edit {preferences-> preferences.remove(key) }
            true
        }catch (e:Exception){
            false
        }
    }

    override suspend fun clearPreferences():Boolean {
        return try{
            dataStore.edit {preferences-> preferences.clear() }
            true
        }catch (e:Exception){
            false
        }
    }
}