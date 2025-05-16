package com.example.client.data.remote.di


import com.example.client.BuildConfig
import com.example.client.data.remote.security.AuthAuthenticator
import com.example.client.data.remote.security.AuthInterceptor
import com.example.client.data.remote.service.AuthenticationService
import com.example.client.data.remote.service.NoteService
import com.example.client.data.remote.service.SocialService
import com.example.client.data.repositories.DataStoreRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHTTPLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideNotesService(retrofit: Retrofit): NoteService =
        retrofit.create(NoteService::class.java)


    @Provides
    fun provideSocialService(retrofit: Retrofit): SocialService =
        retrofit.create(SocialService::class.java)


    @Provides
    fun provideAuthenticationService(retrofit: Retrofit): AuthenticationService =
        retrofit.create(AuthenticationService::class.java)


    @Singleton
    @Provides
    fun provideAuthInterceptor(dataStoreRepository: DataStoreRepository): AuthInterceptor =
        AuthInterceptor(dataStoreRepository)

    @Singleton
    @Provides
    fun provideAuthAuthenticator(dataStoreRepository: DataStoreRepository, usersService: Lazy<AuthenticationService>): AuthAuthenticator =
        AuthAuthenticator(dataStoreRepository,usersService)

}