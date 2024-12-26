package com.example.dictionaryproject.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.AutoMigration
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import com.example.dictionaryproject.app.MyApp
import com.example.dictionaryproject.db.dictionary.DictionaryDb

object MyUtil {

    lateinit var dictionaryDb: DictionaryDb

    const val DICTIONARY_API_BASE_URL = "https://api.dictionaryapi.dev/api/v2/"

    fun createDictionaryDbInstance(context: Context) {
        dictionaryDb = Room.databaseBuilder(
            context,
            DictionaryDb::class.java,
            "dictionary_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}