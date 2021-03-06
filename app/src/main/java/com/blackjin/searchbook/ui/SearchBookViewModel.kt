package com.blackjin.searchbook.ui

import android.text.TextUtils
import androidx.lifecycle.*
import com.blackjin.searchbook.data.repository.SearchBookRepository
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog
import com.blackjin.searchbook.utils.Event
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchBookViewModel(
    private val searchRepository: SearchBookRepository
) : ViewModel() {

    val eventToast = MutableLiveData<Event<String>>()
    val eventShowKeyboard = MutableLiveData<Event<Boolean>>()

    val showLoading = MutableLiveData(false)
    val showMessageText = MutableLiveData("")
    val isVisibleMessageText = MediatorLiveData<Boolean>().apply {
        addSource(showMessageText) { message ->
            postValue(TextUtils.isEmpty(message).not())
        }
    }

    val editSearchText = MutableLiveData("")
    val totalCount = MutableLiveData(0)

    val books = MutableLiveData<List<BookItem>>(emptyList())
    private val mutableBooks = mutableListOf<BookItem>()

    init {
        Dlog.d("SearchBookViewModel init")
        showKeyboard()
        initAutoSearch()
    }

    private fun initAutoSearch() {
        viewModelScope.launch {
            editSearchText.asFlow()
                .debounce(1000L).collect {
                    Dlog.d("debounce : $it")
                    searchBooks()
                }
        }
    }

    private var page = 1
    private var isLoadingFlag = false
    private var isEndFlag = false

    private fun searchBooks() {
        val query = editSearchText.value ?: return
        if (TextUtils.isEmpty(query)) {
            showInitMessage()
            return
        }

        initFlag()

        Dlog.d("page : $page, query : $query")
        viewModelScope.launch {
            searchRepository.searchBook(query, page, object : BaseResponse<Triple<Boolean, Int, List<BookItem>>> {
                override fun onSuccess(data: Triple<Boolean, Int, List<BookItem>>) {

                    val (isEnd, totalCount, books) = data
                    if (totalCount == 0) {
                        clearItems()
                        showMessage("?????? ????????? ????????????.????")
                        return
                    }

                    showTotalCount(totalCount)
                    addAndPostBooks(books)
                }

                override fun onFail(description: String) {
                    clearItems()
                    showMessage(description)
                }

                override fun onError(throwable: Throwable) {
                    clearItems()
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

    fun addNextBooks() {
        Dlog.d("isEndFlag : $isEndFlag, isLoadingFlag : $isLoadingFlag")
        if (isEndFlag || isLoadingFlag) {
            return
        }

        val query = editSearchText.value ?: return
        if (TextUtils.isEmpty(query)) {
            return
        }

        plusPageCount()
        Dlog.d("page : $page, query : $query")
        viewModelScope.launch {
            searchRepository.searchBook(query, page, object : BaseResponse<Triple<Boolean, Int, List<BookItem>>> {
                override fun onSuccess(data: Triple<Boolean, Int, List<BookItem>>) {
                    val (isEnd, totalCount, books) = data
                    isEndFlag = isEnd
                    if (isEnd) {
                        showToast("????????? ????????? ?????????.")
                    } else {
                        addAndPostBooks(books)
                    }
                }

                override fun onFail(description: String) {
                    Dlog.e(description)
                    showToast(description)
                }

                override fun onError(throwable: Throwable) {
                    Dlog.e(throwable.message)
                    showToast(throwable.message)
                }

                override fun onLoading() {
                    showLoading()
                }

                override fun onLoaded() {
                    hideLoading()
                }
            })
        }
    }

    fun changeBookItem(item: BookItem) {
        mutableBooks.forEachIndexed { index, bookItem ->
            if (item.id == bookItem.id) {
                mutableBooks[index] = item
                return@forEachIndexed
            }
        }
        books.postValue(mutableBooks)
    }

    private fun initFlag() {
        page = 1
        isLoadingFlag = false
        isEndFlag = false
    }

    private fun plusPageCount() {
        page++
    }

    private fun addAndPostBooks(books: List<BookItem>) {
        if (books.isNotEmpty()) {
            mutableBooks.addAll(books)
            this.books.postValue(mutableBooks)
        }
    }

    private fun clearItems() {
        showTotalCount(0)
        mutableBooks.clear()
        books.postValue(emptyList())
    }

    private fun showInitMessage() {
        if (books.value?.isEmpty() == true) {
            showMessageText.postValue("??? ????????? ???????????? ????????????\n????????? ?????????.????")
        }
    }

    private fun showToast(message: String?) {
        if (TextUtils.isEmpty(message)) {
            return
        }
        eventToast.postValue(Event(message!!))
    }

    private fun showTotalCount(count: Int) {
        totalCount.postValue(count)
    }

    private fun showLoading() {
        isLoadingFlag = true
        showLoading.postValue(true)
    }

    private fun hideLoading() {
        isLoadingFlag = false
        showLoading.postValue(false)
    }

    private fun showKeyboard() {
        eventShowKeyboard.postValue(Event(true))
    }

    private fun hideKeyboard() {
        eventShowKeyboard.postValue(Event(false))
    }

    private fun showMessage(message: String) {
        showMessageText.postValue(message)
    }

    private fun hideMessage() {
        showMessageText.postValue("")
    }
}