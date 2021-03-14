package com.blackjin.searchbook.ui.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseViewHolder
import com.blackjin.searchbook.databinding.ItemBookBinding
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var onItemClick: ((repoItem: BookItem) -> Unit)? = null

    private val items = mutableListOf<BookItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookViewHolder(parent).apply {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[layoutPosition])
            }
        }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun replaceAll(items: List<BookItem>) {
        Dlog.d("size : ${items.size} -> $items")

        val diffCallback = BookDiffUtilCallback(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.run {
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