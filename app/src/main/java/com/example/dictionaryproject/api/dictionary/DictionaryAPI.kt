package com.example.dictionaryproject.api.dictionary

import com.example.dictionaryproject.model.api_models.dictionary.DictionaryAPIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryAPI {

    @GET("entries/en/{word}")
    suspend fun searchWord(@Path("word") word: String): Response<DictionaryAPIResponse>
}