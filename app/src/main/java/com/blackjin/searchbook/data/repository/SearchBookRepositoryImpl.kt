package com.blackjin.searchbook.data.repository

import com.blackjin.searchbook.data.api.SearchApi
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.ui.model.mapToItem
import com.blackjin.searchbook.utils.Dlog
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SearchBookRepositoryImpl(
    private val searchApi: SearchApi
) : SearchBookRepository {

    companion object {

        private const val SIZE = 50

        private const val TARGET = "title"
    }

    override suspend fun searchBook(query: String, page: Int, callback: BaseResponse<Triple<Boolean, Int, List<BookItem>>>) {
        withContext(Dispatchers.Main) {
            try {
                callback.onLoading()
                withContext(Dispatchers.IO) {
                    val searchResponse = searchApi.searchBook(
                        target = TARGET,
                        query = query,
                        page = page,
                        size = SIZE
                    )

                    val isEnd = searchResponse.meta?.isEnd ?: true
                    val totalCount = searchResponse.meta?.totalCount ?: 0
                    Dlog.d("isEnd : $isEnd, totalCount : $totalCount")

                    val items = searchResponse.documents.mapToItem()
                    callback.onSuccess(Triple(isEnd, totalCount, items))
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    callback.onFail("${e.code()} : ${e.message()}")
                } else {
                    callback.onError(e)
                }
            }
            callback.onLoaded()
        }
    }
}