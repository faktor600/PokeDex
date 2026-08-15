package com.pokedex.core.network.di

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun okHttpClient() = OkHttpClient
        .Builder()
        .build()

    @Provides
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .client(client)
        .addConverterFactory(jsonConverterFactory)
        .build()

    private companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        private val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())
    }
}