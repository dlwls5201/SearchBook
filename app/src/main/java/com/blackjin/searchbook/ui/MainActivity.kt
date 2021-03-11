package com.blackjin.searchbook.ui

import android.os.Bundle
import com.blackjin.searchbook.R
import com.blackjin.searchbook.base.BaseActivity
import com.blackjin.searchbook.databinding.ActivityMainBinding
import com.blackjin.searchbook.ui.detail.DetailFragment
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.ui.search.SearchFragment

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, SearchFragment.newInstance())
            .commit()
    }

    fun goToDetailFragment(bookItem: BookItem) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, DetailFragment.newInstance(bookItem))
            .addToBackStack(null)
            .commit()
    }
}