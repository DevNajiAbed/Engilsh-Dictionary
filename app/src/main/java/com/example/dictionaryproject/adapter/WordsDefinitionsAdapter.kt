package com.example.dictionaryproject.adapter

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionaryproject.databinding.ItemWordDefinitionBinding
import com.example.dictionaryproject.model.WordDefinition
import com.example.dictionaryproject.util.MyUtil

class WordsDefinitionsAdapter : RecyclerView.Adapter<WordsDefinitionsAdapter.WordDefinitionViewHolder>() {

    inner class WordDefinitionViewHolder(val binding: ItemWordDefinitionBinding)
        : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<WordDefinition>() {
        override fun areItemsTheSame(oldItem: WordDefinition, newItem: WordDefinition): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: WordDefinition, newItem: WordDefinition): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordDefinitionViewHolder =
        WordDefinitionViewHolder(
            ItemWordDefinitionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: WordDefinitionViewHolder, position: Int) {
        holder.binding.apply {
            val item = differ.currentList[position]
            tvWord.text = item.word
            tvPhonetic.text = item.phonetic
            tvPartOfSpeech.text = item.partOfSpeech
            tvDefinitions.text = item.definitions

            if(item.soundUrl.isEmpty()){
                btnSound.isVisible = false
            }

            btnSound.setOnClickListener {
                if(MyUtil.hasInternetConnection(it.context)) {
                    MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        try {
                            setDataSource(item.soundUrl)
                            prepare()
                            start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    Toast.makeText(it.context, "No internet connection...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}