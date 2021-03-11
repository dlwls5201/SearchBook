package com.blackjin.searchbook.injection

import com.blackjin.searchbook.data.ApiProvider
import com.blackjin.searchbook.data.repository.SearchBookRepositoryImpl


object Injection {

    fun provideSearchRepository() = SearchBookRepositoryImpl(
        ApiProvider.provideSearchApi()
    )
}