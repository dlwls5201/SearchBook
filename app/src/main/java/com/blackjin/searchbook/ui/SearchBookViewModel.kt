package com.blackjin.searchbook.ui

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.*
import com.blackjin.searchbook.R
import com.blackjin.searchbook.data.repository.SearchBookRepository
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchBookViewModel(
    private val searchRepository: SearchBookRepository
) : ViewModel() {

    val isLoading = MutableLiveData(false)

    val isKeyboard = MutableLiveData(false)

    val messageText = MutableLiveData("")

    val isVisibleMessageText = MediatorLiveData<Boolean>().apply {
        addSource(messageText) { message ->
            postValue(TextUtils.isEmpty(message).not())
        }
    }

    val editSearchText = MutableLiveData("")
    val autoSearchText = editSearchText.asFlow().debounce(1000L)

    val focusedBookItem = MutableLiveData<BookItem>()

    val items = MutableLiveData<List<BookItem>>(emptyList())

    fun showBookItem(bookItem: BookItem) {
        focusedBookItem.postValue(bookItem)
    }

    private var page = 1

    fun searchBooks(context: Context) {
        val query = editSearchText.value ?: return
        if (TextUtils.isEmpty(query)) {
            return
        }

        initSearchFlag()

        Dlog.d("page : $page, query : $query")
        viewModelScope.launch {
            searchRepository.searchBook(query, page, object : BaseResponse<Triple<Boolean, Int, List<BookItem>>> {
                override fun onSuccess(data: Triple<Boolean, Int, List<BookItem>>) {
                    val (isEnd, totalCount, books) = data
                    if (totalCount == 0) {
                        clearItems()
                        showMessage(context.getString(R.string.not_search_result))
                        showMessage("검색 결과가 없습니다.😢")
                        return
                    }

                    if (books.isNotEmpty()) {
                        items.postValue(books)
                    }
                }

                override fun onFail(description: String) {
                    showMessage(description)
                }

                override fun onError(throwable: Throwable) {
                    showMessage(throwable.message ?: "")
                }

                override fun onLoading() {
                    clearItems()
                    showLoading()
                    hideMessage()
                }

                override fun onLoaded() {
                    hideLoading()
                }
            })
        }
    }

    private var isLoadingFlag = false
    private var isEndFlag = false

    fun addNextBooks() {
        Dlog.d("isEndFlag : $isEndFlag, isLoadingFlag : $isLoadingFlag")
        if (isEndFlag || isLoadingFlag) {
            return
        }


        val query = editSearchText.value ?: return
        if (TextUtils.isEmpty(query)) {
            return
        }

        hideKeyboard()
        addPage()

        Dlog.d("page : $page, query : $query")
        viewModelScope.launch {
            searchRepository.searchBook(query, page, object : BaseResponse<Triple<Boolean, Int, List<BookItem>>> {
                override fun onSuccess(data: Triple<Boolean, Int, List<BookItem>>) {
                    val (isEnd, totalCount, books) = data
                    isEndFlag = isEnd
                    if (isEnd) {
                        Dlog.d("마지막 데이터 입니다.")
                    } else {
                        if (books.isNotEmpty()) {
                            items.value?.let { preItems ->
                                val totalItems = preItems.toMutableList().apply {
                                    addAll(books)
                                }
                                items.postValue(totalItems)
                            }
                        }
                    }
                }

                override fun onFail(description: String) {
                    Dlog.e(description)
                }

                override fun onError(throwable: Throwable) {
                    Dlog.e(throwable.message)
                }

                override fun onLoading() {
                    isLoadingFlag = true
                    showLoading()
                }

                override fun onLoaded() {
                    isLoadingFlag = false
                    hideLoading()
                }
            })
        }
    }

    private fun initSearchFlag() {
        isEndFlag = false
        page = 1
    }

    private fun addPage() {
        page++
    }

    private fun clearItems() {
        items.postValue(emptyList())
    }

    fun showInitMessage(context: Context) {
        if (items.value?.isEmpty() == true) {
            messageText.postValue(context.getString(R.string.descriptive_search_message))
        }
    }

    fun showInitKeyboard() {
        if (items.value?.isEmpty() == true) {
            isKeyboard.postValue(true)
        }
    }

    private fun showLoading() {
        isLoading.postValue(true)
    }

    private fun hideLoading() {
        isLoading.postValue(false)
    }

    private fun hideKeyboard() {
        isKeyboard.postValue(false)
    }

    private fun showMessage(message: String) {
        messageText.postValue(message)
    }

    private fun hideMessage() {
        messageText.postValue("")
    }
}