package com.pokedex.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
class ChuckerModule {

    @Provides
    @Singleton
    @Named("chucker")
    fun provideChucker(
        context: Context
    ): Interceptor = ChuckerInterceptor.Builder(context)
        .build()
}