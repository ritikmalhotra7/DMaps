package com.application.dmaps.feat_map.di

import com.application.dmaps.feat_core.data.LocationClientImpl
import com.application.dmaps.feat_core.domain.LocationClient
import com.application.dmaps.feat_map.data.repository.GroupRepositoryImpl
import com.application.dmaps.feat_map.domain.repository.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindsRepo(impl:GroupRepositoryImpl):GroupRepository
}