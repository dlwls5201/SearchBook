package com.blackjin.searchbook.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseFragment
import com.blackjin.searchbook.databinding.FragmentDetailBinding
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.SearchBookViewModel
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog

class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {

    companion object {

        const val ARGUMENT_BOOK_ITEM = "book_item"

        fun newInstance(bookItem: BookItem) = DetailFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_BOOK_ITEM to bookItem)
            }
    }

    private val bookItem: BookItem? by lazy { arguments?.getParcelable(ARGUMENT_BOOK_ITEM) }

    private val searchViewModel by activityViewModels<SearchBookViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchBookViewModel(
                    Injection.provideSearchRepository()
                ) as T
            }
        }
    }

    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onResume() {
        super.onResume()
        Dlog.d("DetailFragment${bookItem?.name} onResume")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dlog.d("DetailFragment${bookItem?.name} onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Dlog.d("DetailFragment${bookItem?.name} onViewCreated")
        binding.model = detailViewModel
        initButton()
    }

    override fun onDestroyView() {
        Dlog.d("DetailFragment${bookItem?.name} onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Dlog.d("DetailFragment${bookItem?.name} onDestroy")
        super.onDestroy()
    }

    override fun onViewModelSetup() {
        super.onViewModelSetup()
        detailViewModel.changedBookItem.observe(viewLifecycleOwner, {
            searchViewModel.changeBookItem(it)
        })
    }

    private fun initButton() {
        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}
