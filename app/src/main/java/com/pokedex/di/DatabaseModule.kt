package com.pokedex.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.pokedex.core_database.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun database(
        context: Context
    ) = Room.databaseBuilder<AppDatabase>(context, "database")
        .setDriver(AndroidSQLiteDriver())
        .build()

    @Provides
    fun dao(
        appDatabase: AppDatabase
    ) = appDatabase.pokemonDao()

    @Provides
    fun speciesDao(
        appDatabase: AppDatabase
    ) = appDatabase.pokemonSpeciesDao()
}