package com.example.dictionaryproject.model.api_models.dictionary

data class DictionaryAPIResponseItem(
    val license: License,
    val meanings: List<Meaning>,
    val phonetic: String?,
    val phonetics: List<Phonetic>,
    val sourceUrls: List<String>,
    val word: String
)