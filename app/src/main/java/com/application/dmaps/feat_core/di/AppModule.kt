package com.application.dmaps.feat_core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.application.dmaps.feat_auth.utils.AuthConstants.AUTHENTICATION_TOKEN
import com.application.dmaps.feat_core.data.SocketManager
import com.application.dmaps.feat_core.data.remote.AppApi
import com.application.dmaps.feat_core.data.remote.BASE_URL
import com.application.dmaps.feat_core.data.remote.SOCKET_URL
import com.application.dmaps.feat_core.utils.Constants.DATA_STORE_NAME
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext ctx:Context):FusedLocationProviderClient = FusedLocationProviderClient(ctx)

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providesSocketManager(dataStore: DataStore<Preferences>):SocketManager{
        val token:String = runBlocking {
            dataStore.data.first().asMap()
                .getOrDefault(stringPreferencesKey(AUTHENTICATION_TOKEN), "").toString()
        }
        return SocketManager(SOCKET_URL,token)
    }

    @Provides
    @Singleton
    fun provideApi(dataStore: DataStore<Preferences>): AppApi {
        val interceptor = Interceptor { chain ->
            val token = runBlocking {
                dataStore.data.first().asMap()
                    .getOrDefault(stringPreferencesKey(AUTHENTICATION_TOKEN), "")
            }
            val request = chain.request()
            val modifiedRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            val response = chain
                .proceed(modifiedRequest)
            response
        }
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder().addInterceptor(interceptor)
                    .addInterceptor(loggingInterceptor).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppApi::class.java)
    }
}