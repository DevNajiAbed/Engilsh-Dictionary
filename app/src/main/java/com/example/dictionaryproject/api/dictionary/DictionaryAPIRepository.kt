package com.example.dictionaryproject.api.dictionary

import com.example.dictionaryproject.api.Resource
import com.example.dictionaryproject.model.api_models.dictionary.DictionaryAPIResponse
import com.example.dictionaryproject.model.api_models.dictionary.DictionaryAPIResponseItem
import com.example.dictionaryproject.util.MyUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DictionaryAPIRepository {

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).build()
    }

    private val api: DictionaryAPI by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(MyUtil.DICTIONARY_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DictionaryAPI::class.java)
    }

    suspend fun searchWord(word: String): Resource<DictionaryAPIResponse> {
        val response = api.searchWord(word)
        if(response.isSuccessful && response.body() != null)
            return Resource.Success(response.body()!!)
        return Resource.Error(response.message())
    }
}