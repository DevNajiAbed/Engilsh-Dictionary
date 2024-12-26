package com.example.dictionaryproject.db.dictionary.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dictionaryproject.model.SearchHistoryItem

@Dao
interface SearchHistoryDao {

    @Upsert
    suspend fun upsertSearchHistoryItem(item: SearchHistoryItem)

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    fun getAllSearchHistory(): LiveData<List<SearchHistoryItem>>

    @Query("SELECT * FROM search_history WHERE searchQuery LIKE :word ORDER BY id DESC")
    suspend fun searchInSearchHistory(word: String): List<SearchHistoryItem>

    @Delete
    suspend fun deleteSearchHistoryItem(item: SearchHistoryItem)

    @Query("SELECT COUNT(*) FROM search_history WHERE searchQuery = :searchQuery")
    suspend fun getCountOfSearchQuery(searchQuery: String): Int
}