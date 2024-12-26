package com.example.dictionaryproject.db.dictionary.repo

import com.example.dictionaryproject.db.dictionary.dao.SearchHistoryDao
import com.example.dictionaryproject.model.SearchHistoryItem
import com.example.dictionaryproject.util.MyUtil

object SearchHistoryRepository {

    private var dao: SearchHistoryDao = MyUtil.dictionaryDb.searchHistoryDao

    suspend fun upsertSearchHistoryItem(item: SearchHistoryItem) {
        item.apply { searchQuery = searchQuery.lowercase() }
        if (getCountOfSearchQuery(item.searchQuery) == 0)
            dao.upsertSearchHistoryItem(item)
    }

    fun getAllSearchHistory() = dao.getAllSearchHistory()

    suspend fun searchInSearchHistory(word: String) = dao.searchInSearchHistory(word)

    suspend fun deleteSearchHistoryItem(item: SearchHistoryItem) {
        dao.deleteSearchHistoryItem(item)
    }

    private suspend fun getCountOfSearchQuery(searchQuery: String) =
        dao.getCountOfSearchQuery(searchQuery)
}