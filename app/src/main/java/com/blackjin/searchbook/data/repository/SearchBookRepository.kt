package com.blackjin.searchbook.data.repository

import com.blackjin.searchbook.ui.model.BookItem
import com.example.toyproject.data.base.BaseResponse

interface SearchBookRepository {

    suspend fun searchBook(query: String, page: Int, callback: BaseResponse<Triple<Boolean, Int, List<BookItem>>>)
}