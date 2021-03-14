package com.blackjin.searchbook.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseViewHolder
import com.blackjin.searchbook.databinding.ItemBookBinding
import com.blackjin.searchbook.ui.model.BookItem

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var onItemClick: ((repoItem: BookItem) -> Unit)? = null

    private val books = mutableListOf<BookItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent).apply {
            itemView.setOnClickListener {
                onItemClick?.invoke(books[layoutPosition])
            }
        }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    fun replaceAll(items: List<BookItem>) {
        val diffCallback = BookDiffUtilCallback(books, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        books.run {
            clear()
            addAll(items)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    class BookViewHolder(parent: ViewGroup) : BaseViewHolder<ItemBookBinding, BookItem>(
        parent, R.layout.item_book
    ) {
        override fun bind(data: BookItem) {
            binding?.item = data
        }
    }

    class BookDiffUtilCallback(
        private val oldList: List<BookItem>, private val newList: List<BookItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}