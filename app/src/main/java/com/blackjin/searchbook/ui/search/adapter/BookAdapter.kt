package com.blackjin.searchbook.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseViewHolder
import com.blackjin.searchbook.databinding.ItemBookBinding
import com.blackjin.searchbook.ui.model.BookItem

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var items: MutableList<BookItem> = mutableListOf()

    var onItemClick: ((repoItem: BookItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent).apply {
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                onItemClick?.invoke(item)
            }
        }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(items: List<BookItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    class BookViewHolder(parent: ViewGroup) : BaseViewHolder<ItemBookBinding, BookItem>(
        parent, R.layout.item_book
    ) {

        override fun bind(data: BookItem) {
            binding?.item = data
        }
    }
}