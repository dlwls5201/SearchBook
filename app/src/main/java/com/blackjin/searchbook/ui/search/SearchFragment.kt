package com.blackjin.searchbook.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseFragment
import com.blackjin.searchbook.databinding.FragmentSearchBinding
import com.blackjin.searchbook.ext.toast
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.MainActivity
import com.blackjin.searchbook.ui.SearchBookViewModel
import com.blackjin.searchbook.ui.search.adapter.BookAdapter
import com.blackjin.searchbook.utils.AppUtils

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    companion object {

        fun newInstance() = SearchFragment()
    }

    private val bookAdapter by lazy {
        BookAdapter().apply {
            onItemClick = { bookItem ->
                (requireActivity() as MainActivity).goToDetailFragment(bookItem)
                hideSearchKeyboard()
            }
        }
    }

    private val searchBookViewModel by activityViewModels<SearchBookViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchBookViewModel(
                    Injection.provideSearchRepository()
                ) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.model = searchBookViewModel
        initRecyclerView()
    }

    override fun onViewModelSetup() {
        searchBookViewModel.books.observe(viewLifecycleOwner, {
            bookAdapter.replaceAll(it)
        })

        searchBookViewModel.eventShowKeyboard.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { isShowKeyboard ->
                if (isShowKeyboard) {
                    showSearchKeyboard()
                } else {
                    hideSearchKeyboard()
                }
            }
        })

        searchBookViewModel.eventToast.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { message ->
                toast(message)
            }
        })
    }

    private fun initRecyclerView() {
        with(binding.listSearchBook) {
            adapter = bookAdapter
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val lm = recyclerView.layoutManager
                        if (lm is LinearLayoutManager) {
                            if (lm.findLastCompletelyVisibleItemPosition() ==
                                recyclerView.adapter?.itemCount?.minus(1)
                            ) {
                                searchBookViewModel.addNextBooks()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun showSearchKeyboard() {
        activity?.let { _activity ->
            binding.etSearch.requestFocus()
            AppUtils.showSoftKeyBoard(_activity)
        }
    }

    private fun hideSearchKeyboard() {
        activity?.let { _activity ->
            AppUtils.hideSoftKeyBoard(_activity)
        }
    }

}