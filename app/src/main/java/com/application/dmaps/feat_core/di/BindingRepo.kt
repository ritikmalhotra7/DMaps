package com.application.dmaps.feat_core.di

import com.application.dmaps.feat_core.data.LocationClientImpl
import com.application.dmaps.feat_core.data.repository.CoreRepositoryImpl
import com.application.dmaps.feat_core.domain.LocationClient
import com.application.dmaps.feat_core.domain.repository.CoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingRepo {
    @Binds
    @Singleton
    abstract fun bindsLocationClient(impl: LocationClientImpl): LocationClient

    @Binds
    @Singleton
    abstract fun bindsRepo(repo:CoreRepositoryImpl):CoreRepository
}