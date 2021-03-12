package com.blackjin.searchbook.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseViewHolder
import com.blackjin.searchbook.databinding.ItemBookBinding
import com.blackjin.searchbook.ui.model.BookItem

class BookAdapter : ListAdapter<BookItem, BookAdapter.BookViewHolder>(BookDiffUtilCallback()) {

    var onItemClick: ((repoItem: BookItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent).apply {
            itemView.setOnClickListener {
                //TODO check adapter position deprecated
                onItemClick?.invoke(currentList[adapterPosition])
            }
        }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount() = currentList.size

    fun replaceAll(items: List<BookItem>) {
        submitList(items)
    }

    class BookViewHolder(parent: ViewGroup) : BaseViewHolder<ItemBookBinding, BookItem>(
        parent, R.layout.item_book
    ) {

        override fun bind(data: BookItem) {
            binding?.item = data
        }
    }

    class BookDiffUtilCallback : DiffUtil.ItemCallback<BookItem>() {

        override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem) =
            oldItem == newItem
    }
}