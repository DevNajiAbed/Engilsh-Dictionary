package com.example.dictionaryproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_definition")
data class WordDefinition(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val definitions: String,
    val soundUrl: String,
    val url: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
