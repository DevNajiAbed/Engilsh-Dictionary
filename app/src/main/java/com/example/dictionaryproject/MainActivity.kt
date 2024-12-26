package com.example.dictionaryproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionaryproject.adapter.SearchHistoryAdapter
import com.example.dictionaryproject.adapter.WordsDefinitionsAdapter
import com.example.dictionaryproject.api.Resource
import com.example.dictionaryproject.databinding.ActivityMainBinding
import com.example.dictionaryproject.view_model.activity.MainViewModel
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var wordsDefinitionsAdapter: WordsDefinitionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            searchView.setupWithSearchBar(searchBar)

            rvSearchHistory.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                this@MainActivity.searchHistoryAdapter = prepareSearchHistoryAdapter()
                adapter = this@MainActivity.searchHistoryAdapter
            }

            rvWordsDefinitions.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                this@MainActivity.wordsDefinitionsAdapter = prepareWordsDefinitionsAdapter()
                adapter = this@MainActivity.wordsDefinitionsAdapter
            }

            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    handleSearchAction()
                    true
                }
        }

        observeSearchHistoryLiveData()
        observeSearchWordLiveData()
        observeLocalSavedWordDefinitionsLiveData()
    }

    private fun observeLocalSavedWordDefinitionsLiveData() {
        viewModel.localSavedWordsDefinitions.observe(this) { wordDefinitions ->
            wordsDefinitionsAdapter.differ.submitList(wordDefinitions)
            hideProgressBar()
            showWordsDefinitionsRV()
        }
    }

    private fun prepareWordsDefinitionsAdapter(): WordsDefinitionsAdapter =
        WordsDefinitionsAdapter().apply {
            viewModel.searchWordLiveData.value?.let {
                it.response?.let { list ->
                    differ.submitList(list)
                }
            }
        }

    private fun observeSearchHistoryLiveData() {
        viewModel.currentSearchHistoryLiveData.observe(this) { currentSearchHistory ->
            searchHistoryAdapter.differ.submitList(currentSearchHistory)
        }
    }

    private fun observeSearchWordLiveData() {
        viewModel.searchWordLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.response?.let { response ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            wordsDefinitionsAdapter.differ.submitList(response)
                            withContext(Dispatchers.Main) {
                                binding.rvWordsDefinitions.scrollToPosition(0)
                            }
                        }
                    }
                    hideProgressBar()
                    showWordsDefinitionsRV()
                }

                is Resource.Error -> {
                    resource.message?.let { msg ->
                        Log.e("nji", "Dictionary API Error Call: $msg")
                    }
                    showSnackbarMsg()
                    hideProgressBar()
                    showWordsDefinitionsRV()
                }

                is Resource.Loading -> {
                    showProgressBar()
                    hideWordsDefinitionsRV()
                }

                is Resource.Empty -> {
                    hideProgressBar()
                    showWordsDefinitionsRV()
                }
            }
        }
    }

    private fun showSnackbarMsg() {
        Snackbar.make(
            binding.root,
            "It seems that this input has no definitions found.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showProgressBar() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgressBar() {
        binding.progressBar.isVisible = false
    }

    private fun showWordsDefinitionsRV() {
        binding.rvWordsDefinitions.isVisible = true
    }

    private fun hideWordsDefinitionsRV() {
        binding.rvWordsDefinitions.isVisible = false
    }

    private fun ActivityMainBinding.prepareSearchHistoryAdapter() =
        SearchHistoryAdapter({ item ->
            val searchQuery = item.searchQuery
            searchBar.text = searchQuery
            searchView.editText.setText(searchQuery)
            searchView.hide()
            search(searchQuery.trim())
        }) { item ->
            viewModel.apply {
                deleteSearchHistoryItem(item)
            }
        }.apply {
            differ.submitList(viewModel.allSearchHistoryList)
        }

    private var searchJob: Job? = null
    private val searchHistoryTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val query = s.toString().trim()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                viewModel.searchInSearchHistory(query)
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView
            .editText
            .addTextChangedListener(searchHistoryTextWatcher)
    }

    override fun onPause() {
        super.onPause()
        binding.searchView
            .editText
            .removeTextChangedListener(searchHistoryTextWatcher)
    }

    private fun ActivityMainBinding.handleSearchAction() {
        val searchQuery = searchView.text.toString().trim()
        searchBar.text = searchQuery
        searchView.hide()
        search(searchQuery)
    }

    private fun search(searchQuery: String) {
        if (searchQuery.isNotEmpty())
            viewModel.apply {
                upsertSearchHistoryItem(searchQuery)
                searchWord(searchQuery)
            }
    }

    override fun onBackPressed() {
        if (binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWING
            || binding.searchView.currentTransitionState == SearchView.TransitionState.SHOWN
        ) {
            binding.searchView.hide()
            return
        }
        super.onBackPressed()
    }
}