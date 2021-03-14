package com.blackjin.searchbook.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseActivity
import com.blackjin.searchbook.databinding.ActivityMainBinding
import com.blackjin.searchbook.injection.Injection
import com.blackjin.searchbook.ui.detail.DetailFragment
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.ui.search.SearchFragment

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val searchBookViewModel by viewModels<SearchBookViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchBookViewModel(
                    Injection.provideSearchRepository()
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initFragment()
        }
    }

    override fun onViewModelSetup() {
        super.onViewModelSetup()
        searchBookViewModel.eventShowDetailFragment.observe(this, {
            it.getContentIfNotHandled()?.let { bookItem ->
                goToDetailFragment(bookItem)
            }
        })
    }

    private fun goToDetailFragment(bookItem: BookItem) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, DetailFragment.newInstance(bookItem))
            .addToBackStack(null)
            .commit()
    }

    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, SearchFragment.newInstance())
            .commit()
    }
}