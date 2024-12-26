package com.example.dictionaryproject.db.dictionary.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.dictionaryproject.model.WordDefinition

@Dao
interface WordDefinitionDao {

    @Upsert
    suspend fun upsertWordDefinition(wordDefinition: WordDefinition)

    @Query("SELECT * FROM word_definition WHERE word = :word")
    suspend fun getDefinitionOfWord(word: String): List<WordDefinition>

    @Query("SELECT COUNT(*) FROM word_definition WHERE word = :word")
    suspend fun getDefinitionsCountOfWord(word: String): Int
}