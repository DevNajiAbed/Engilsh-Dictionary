package com.example.dictionaryproject.model.api_models.dictionary

data class Phonetic(
    val audio: String,
    val license: License,
    val sourceUrl: String,
    val text: String
)