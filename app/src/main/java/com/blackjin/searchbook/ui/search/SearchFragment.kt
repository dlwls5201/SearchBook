package com.blackjin.searchbook.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseFragment
import com.blackjin.searchbook.databinding.FragmentSearchBinding
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.MainActivity
import com.blackjin.searchbook.ui.SearchBookViewModel
import com.blackjin.searchbook.ui.search.adapter.BookAdapter
import com.blackjin.searchbook.utils.AppUtils
import com.blackjin.searchbook.utils.Dlog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    companion object {

        fun newInstance() = SearchFragment()
    }

    private val bookAdapter by lazy {
        BookAdapter().apply {
            onItemClick = { bookItem ->
                (requireActivity() as MainActivity).goToDetailFragment(bookItem)
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
        showInitMessage()
        showInitKeyboard()
    }

    override fun onViewModelSetup() {
        searchBookViewModel.items.observe(viewLifecycleOwner, {
            bookAdapter.replaceAll(it)
        })

        searchBookViewModel.isKeyboard.observe(viewLifecycleOwner, { showKeyboard ->
            activity?.let { _activity ->
                if (showKeyboard) {
                    binding.etSearch.requestFocus()
                    AppUtils.showSoftKeyBoard(_activity)
                } else {
                    AppUtils.hideSoftKeyBoard(_activity)
                }
            }
        })

        lifecycleScope.launch {
            searchBookViewModel.autoSearchText.collect {
                Dlog.d("debounce : $it")
                context?.let { _context ->
                    searchBookViewModel.searchBooks(_context)
                }
            }
        }
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

    private fun showInitMessage() {
        context?.let {
            searchBookViewModel.showInitMessage(it)
        }
    }

    private fun showInitKeyboard() {
        searchBookViewModel.showInitKeyboard()
    }
}