package com.blackjin.searchbook.data.repository

import com.blackjin.searchbook.data.source.remote.SearchApi
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.ui.model.mapToItem
import com.blackjin.searchbook.utils.Dlog
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SearchBookRepositoryImpl(
    private val searchApi: SearchApi
) {

    suspend fun searchBook(query: String, callback: BaseResponse<Pair<Int, List<BookItem>>>) {
        withContext(Dispatchers.Main) {
            try {
                callback.onLoading()
                withContext(Dispatchers.IO) {
                    val searchResponse = searchApi.searchBook(
                        query = query,
                        page = 1,
                        size = 50
                    )

                    val isEnd = searchResponse.meta?.isEnd
                    val totalCount = searchResponse.meta?.totalCount ?: 0
                    Dlog.d("isEnd : $isEnd")

                    val items = searchResponse.documents.mapToItem()
                    callback.onSuccess(Pair(totalCount, items))
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