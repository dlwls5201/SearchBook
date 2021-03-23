package com.blackjin.searchbook.ui

import android.os.Bundle
import android.os.Handler
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
import com.blackjin.searchbook.utils.Dlog

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
            intButton()
        }
    }

    private var flag = 0

    private fun intButton() {
        with(binding) {
            btn1.setOnClickListener {
                initFragment()
            }
            btn2.setOnClickListener {
                Dlog.d("fragments : ${supportFragmentManager.fragments.size}")
                //supportFragmentManager.popBackStack("blackjin", 0)
                //supportFragmentManager.popBackStack("blackjin", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                Handler().postDelayed({
                    Dlog.d("fragments : ${supportFragmentManager.fragments.size}")
                    Dlog.d("backStackEntryCount : ${supportFragmentManager.backStackEntryCount}")
                    val findFragment = supportFragmentManager.findFragmentById(R.id.fl_container)
                    Dlog.d("findFragment : $findFragment")
                }, 500)
            }
            btn3.setOnClickListener {
                flag++
                val bookItem = BookItem(
                    thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F506412%3Ftimestamp%3D20210303152559",
                    name = "나무 $flag",
                    date = "2003-06-03",
                    publisher = "열린책들"
                )
                goToDetailFragment(bookItem)
            }
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