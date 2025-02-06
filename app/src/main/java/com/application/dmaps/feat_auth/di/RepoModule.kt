package com.application.dmaps.feat_auth.di

import com.application.dmaps.feat_auth.data.repository.AuthRepositoryImpl
import com.application.dmaps.feat_auth.domian.repository.AuthRepository
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
    abstract fun bindsRepo(repo: AuthRepositoryImpl): AuthRepository
}