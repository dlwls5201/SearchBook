package com.blackjin.searchbook.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseFragment
import com.blackjin.searchbook.databinding.FragmentDetailBinding
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.SearchViewModel
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog

class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {

    companion object {

        private const val ARGUMENT_BOOK_ITEM = "book_item"

        fun newInstance(bookItem: BookItem) = DetailFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_BOOK_ITEM to bookItem)
            }
    }

    private val searchViewModel by activityViewModels<SearchViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    Injection.provideSearchRepository()
                ) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = searchViewModel
        checkAndShowBookItem()
        initButton()
    }

    private fun checkAndShowBookItem() {
        val bookItem = arguments?.getParcelable<BookItem>(ARGUMENT_BOOK_ITEM)
            ?: error("bookItem must not be null")

        Dlog.d("BookItem : ${bookItem.name}")
        searchViewModel.showBookItem(bookItem)
    }

    private fun initButton() {
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}
