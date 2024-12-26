package com.example.dictionaryproject.db.dictionary.repo

import com.example.dictionaryproject.model.WordDefinition
import com.example.dictionaryproject.util.MyUtil

object WordDefinitionRepository {

    private val dao = MyUtil.dictionaryDb.wordDefinitionDao

    suspend fun upsertWordDefinition(wordDefinition: WordDefinition) {
        dao.upsertWordDefinition(wordDefinition)
    }

    suspend fun getDefinitionOfWord(word: String) = dao.getDefinitionOfWord(word)

    suspend fun isThisWordHasLocalSavedDefinitions(word: String) =
        dao.getDefinitionsCountOfWord(word) > 0
}