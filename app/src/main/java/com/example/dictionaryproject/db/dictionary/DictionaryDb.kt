package com.example.dictionaryproject.db.dictionary

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dictionaryproject.db.dictionary.dao.SearchHistoryDao
import com.example.dictionaryproject.db.dictionary.dao.WordDefinitionDao
import com.example.dictionaryproject.model.SearchHistoryItem
import com.example.dictionaryproject.model.WordDefinition

@Database(
    entities = [
        SearchHistoryItem::class,
        WordDefinition::class
    ],
    version = 4
)
abstract class DictionaryDb : RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao
    abstract val wordDefinitionDao: WordDefinitionDao
}