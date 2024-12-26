package com.example.dictionaryproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryItem (
    var searchQuery: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)