package com.example.client.data.remote.di


import com.example.client.BuildConfig
import com.example.client.data.remote.service.NominatimService
import com.example.client.data.remote.service.NoteService
import com.example.client.data.remote.service.SocialService
import com.example.client.data.remote.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NominatimRetrofit

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
    @MainRetrofit
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @NominatimRetrofit
    fun provideNominatimRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                okHttpClient.newBuilder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "TFG_Client/1.0 (saavedra.mateo.walter@gmail.com)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()


    @Provides
    fun provideNoteService(@MainRetrofit retrofit: Retrofit): NoteService =
        retrofit.create(NoteService::class.java)

    @Provides
    fun provideUserService(@MainRetrofit retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    fun provideNominatimService(@NominatimRetrofit retrofit: Retrofit): NominatimService =
        retrofit.create(NominatimService::class.java)


    @Provides
    fun provideSocialService(@MainRetrofit retrofit: Retrofit): SocialService =
        retrofit.create(SocialService::class.java)

}