package com.pokedex.core.network.di

import com.pokedex.core.network.AppJson
import com.pokedex.core.network.ConnectionLostInterceptor
import com.pokedex.core.network.DefaultNetworkEvents
import com.pokedex.core.network.NetworkEvents
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkEvents(): NetworkEvents = DefaultNetworkEvents()

    @Provides
    @Singleton
    fun okHttpClient(
        @Named("chucker") interceptor: Interceptor,
        networkEvents: NetworkEvents
    ) = OkHttpClient
        .Builder()
        // Outermost: sees the real exception, and Chucker still logs the failed call.
        .addInterceptor(ConnectionLostInterceptor(networkEvents))
        .addInterceptor(interceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .client(client)
        .addConverterFactory(jsonConverterFactory)
        .build()

    private companion object {
        private val jsonConverterFactory = AppJson.asConverterFactory("application/json".toMediaType())
    }
}
