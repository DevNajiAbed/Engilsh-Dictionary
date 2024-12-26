package com.example.dictionaryproject.view_model.activity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryproject.api.Resource
import com.example.dictionaryproject.api.dictionary.DictionaryAPIRepository
import com.example.dictionaryproject.app.MyApp
import com.example.dictionaryproject.db.dictionary.repo.SearchHistoryRepository
import com.example.dictionaryproject.db.dictionary.repo.WordDefinitionRepository
import com.example.dictionaryproject.model.SearchHistoryItem
import com.example.dictionaryproject.model.WordDefinition
import com.example.dictionaryproject.model.api_models.dictionary.DictionaryAPIResponse
import com.example.dictionaryproject.util.MyUtil
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class MainViewModel(
    app: Application
) : AndroidViewModel(app) {

    var allSearchHistoryList = ArrayList<SearchHistoryItem>()
        private set
    private val _currentSearchHistoryLiveData = MutableLiveData<List<SearchHistoryItem>>()
    val currentSearchHistoryLiveData: LiveData<List<SearchHistoryItem>> =
        _currentSearchHistoryLiveData

    private val _searchWordLiveData =
        MutableLiveData<Resource<List<WordDefinition>>>(Resource.Empty())
    val searchWordLiveData: LiveData<Resource<List<WordDefinition>>> = _searchWordLiveData
    private var currentSearchWord = ""

    private val _localSavedWordsDefinitions = MutableLiveData<List<WordDefinition>>()
    val localSavedWordsDefinitions: LiveData<List<WordDefinition>> = _localSavedWordsDefinitions

    init {
        getAllSearchHistory()
    }

    private fun getAllSearchHistory() {
        SearchHistoryRepository.getAllSearchHistory()
            .observeForever { list ->
                allSearchHistoryList = ArrayList(list)
            }
    }

    fun searchInSearchHistory(query: String) = viewModelScope.launch {
        if (query.isEmpty()) {
            _currentSearchHistoryLiveData.value = allSearchHistoryList
            return@launch
        }
        val word = "%$query%"
        _currentSearchHistoryLiveData.value = SearchHistoryRepository.searchInSearchHistory(word)
    }

    fun deleteSearchHistoryItem(item: SearchHistoryItem) = viewModelScope.launch {
        SearchHistoryRepository.deleteSearchHistoryItem(item)
        deleteItemFromCurrentSearchHistoryList(item)
    }

    private fun deleteItemFromCurrentSearchHistoryList(item: SearchHistoryItem) {
        _currentSearchHistoryLiveData.apply {
            value?.let {
                val currentList = ArrayList(it)
                currentList.remove(item)
                value = currentList.toList()
            }
        }
    }

    fun upsertSearchHistoryItem(searchQuery: String) = viewModelScope.launch {
        SearchHistoryRepository.upsertSearchHistoryItem(SearchHistoryItem(searchQuery))
    }

    fun searchWord(word: String) = viewModelScope.launch {
        val word = word.lowercase()

        _searchWordLiveData.value = Resource.Loading()
        if (MyUtil.hasInternetConnection(getApplication())) {
            if (currentSearchWord != word) {
                currentSearchWord = word
                _searchWordLiveData.apply {
                    val response = DictionaryAPIRepository.searchWord(word)
                    val resultList = handleResponse(response)
                    value = Resource.Empty(resultList)
                    saveInLocalDb(resultList)
                }
            } else {
                _searchWordLiveData.value = Resource.Empty()
            }
        } else {
            val localSavedData = WordDefinitionRepository.getDefinitionOfWord(word)
            if (localSavedData.isNotEmpty()) {
                currentSearchWord = word
                _localSavedWordsDefinitions.value = localSavedData
            } else {
                Toast.makeText(getApplication(), "No internet connection...", Toast.LENGTH_SHORT)
                    .show()
                _searchWordLiveData.value = Resource.Empty()
            }
        }
    }

    private suspend fun saveInLocalDb(resultList: List<WordDefinition>) {
        if (!WordDefinitionRepository.isThisWordHasLocalSavedDefinitions(currentSearchWord))
            for (wordDefinition in resultList) {
                WordDefinitionRepository.upsertWordDefinition(wordDefinition)
            }
    }

    private fun handleResponse(response: Resource<DictionaryAPIResponse>): List<WordDefinition> {
        var list = emptyList<WordDefinition>()
        if (response is Resource.Success) {
            list = handleSucceededResponse(response)
        } else if (response is Resource.Error) {
            _searchWordLiveData.value = Resource.Error(response.message!!)
        }
        return list
    }

    private fun handleSucceededResponse(response: Resource.Success<DictionaryAPIResponse>): List<WordDefinition> {
        val resultList = ArrayList<WordDefinition>()
        response.response?.let { apiResponse ->
            for (item in apiResponse) {
                val partOfSpeech = item.meanings[0].partOfSpeech

                val definitionsList = item.meanings[0].definitions
                val definitions = StringBuilder()
                for (item2 in definitionsList.indices) {
                    val definition = definitionsList[item2]
                    definitions.append("${item2 + 1}) ${definition.definition}\n")
                    definition.example?.let {
                        definitions.append("\t- Example: $it\n\n")
                    } ?: definitions.append("\n\n")
                }

                val soundUrl = if (item.phonetics.isNotEmpty())
                    item.phonetics[0].audio
                else
                    ""

                resultList.add(
                    WordDefinition(
                        item.word,
                        item.phonetic ?: "",
                        partOfSpeech,
                        definitions.toString().trim(),
                        soundUrl,
                        item.license.url
                    )
                )
            }

            _searchWordLiveData.value = Resource.Success(resultList.toList())
        }
        return resultList.toList()
    }
}