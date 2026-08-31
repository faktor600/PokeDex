package com.pokedex.core_database.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.PrimaryKey
import androidx.room3.Entity

/**
 * Сущность, представляющая объект с именем и URL.
 * Используется как базовый компонент для различных ответов (responses) в приложении PokeDex.
 */
@Entity(tableName = "item_with_url_responses")
data class ItemWithUrlResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String
)
