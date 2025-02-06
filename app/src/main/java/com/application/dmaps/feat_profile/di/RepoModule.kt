package com.application.dmaps.feat_profile.di

import com.application.dmaps.feat_profile.data.repository.ProfileRepositoryImpl
import com.application.dmaps.feat_profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindsRepo(repoImpl:ProfileRepositoryImpl):ProfileRepository
}