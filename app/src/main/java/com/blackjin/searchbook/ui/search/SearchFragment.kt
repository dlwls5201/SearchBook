package com.blackjin.searchbook.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseFragment
import com.blackjin.searchbook.databinding.FragmentSearchBinding
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.MainActivity
import com.blackjin.searchbook.ui.SearchViewModel
import com.blackjin.searchbook.ui.search.adapter.BookAdapter
import com.blackjin.searchbook.utils.AppUtils
import com.blackjin.searchbook.utils.Dlog

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    companion object {

        fun newInstance() = SearchFragment()
    }

    private val repoAdapter by lazy {
        BookAdapter().apply {
            onItemClick = { bookItem ->
                (requireActivity() as MainActivity).goToDetailFragment(bookItem)
            }
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
        Dlog.d("searchViewModel : $searchViewModel")
        initRecyclerView()
        initEditText()
        showInitMessage()
    }

    override fun onViewModelSetup() {
        searchViewModel.items.observe(viewLifecycleOwner, {
            repoAdapter.setItems(it)
        })

        searchViewModel.isKeyboard.observe(viewLifecycleOwner, { showKeyboard ->
            activity?.let { _activity ->
                if (showKeyboard) {
                    binding.etSearch.requestFocus()
                    AppUtils.showSoftKeyBoard(_activity)
                } else {
                    AppUtils.hideSoftKeyBoard(_activity)
                }
            }
        })
    }

    private fun initRecyclerView() {
        with(binding.listSearchBook) {
            adapter = repoAdapter
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun initEditText() {
        binding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        context?.let {
                            searchViewModel.searchRepository(it)
                        }
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        })
    }

    private fun showInitMessage() {
        context?.let {
            searchViewModel.showInitMessage(it)
        }
    }
}