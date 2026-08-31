package com.pokedex.core_database.database

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(

    version = 1
)
abstract class AppDatabase: RoomDatabase()