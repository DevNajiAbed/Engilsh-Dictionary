package com.example.dictionaryproject.model.api_models.dictionary

data class Definition(
    val antonyms: List<String>,
    val definition: String,
    val example: String?,
    val synonyms: List<Any>
)