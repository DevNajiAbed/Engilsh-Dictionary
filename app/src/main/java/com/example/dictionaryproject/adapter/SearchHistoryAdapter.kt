package com.example.dictionaryproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dictionaryproject.databinding.ItemSearchHistoryBinding
import com.example.dictionaryproject.model.SearchHistoryItem

class SearchHistoryAdapter(
    val onItemClick: (item: SearchHistoryItem) -> Unit,
    val onDelete: (item: SearchHistoryItem) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryItemViewHolder>() {

    inner class  SearchHistoryItemViewHolder(val binding: ItemSearchHistoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<SearchHistoryItem>() {
        override fun areItemsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryItemViewHolder =
        SearchHistoryItemViewHolder(
            ItemSearchHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {
        holder.binding.apply {
            val item = differ.currentList[position]
            tvSearchItem.text = item.searchQuery

            root.setOnClickListener {
                onItemClick(item)
            }

            btnDeleteSearchItem.setOnClickListener {
                onDelete(item)
            }
        }
    }
}