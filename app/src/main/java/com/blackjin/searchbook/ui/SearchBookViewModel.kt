package com.blackjin.searchbook.ui

import android.text.TextUtils
import androidx.lifecycle.*
import com.blackjin.searchbook.data.repository.SearchBookRepository
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.flow.collect
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

    val focusedBookItemData = MutableLiveData<BookItem>()

    val bookItemsData = MutableLiveData<List<BookItem>>(emptyList())

    fun showBookItem(bookItem: BookItem) {
        focusedBookItemData.postValue(bookItem)
    }

    private var page = 1

    init {
        viewModelScope.launch {
            autoSearchText.collect {
                Dlog.d("debounce : $it")
                searchBooks()
            }
        }
    }

    private fun searchBooks() {
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
                        //showMessage(context.getString(R.string.not_search_result))
                        showMessage("검색 결과가 없습니다.😢")
                        return
                    }

                    if (books.isNotEmpty()) {
                        bookItemsData.postValue(books)
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
                            bookItemsData.value?.let { preItems ->
                                val totalItems = preItems.toMutableList().apply {
                                    addAll(books)
                                }
                                bookItemsData.postValue(totalItems)
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

    fun changeLikeFocusedItem() {
        val bookItems = bookItemsData.value?.toMutableList() ?: return
        val focusedBookItem = focusedBookItemData.value ?: return

        val isLike = focusedBookItem.isLike
        val changedBookItem = focusedBookItem.copy(isLike = isLike.not())

        bookItems.forEachIndexed { index, bookItem ->
            if (changedBookItem.id == bookItem.id) {
                bookItems[index] = changedBookItem
                return@forEachIndexed
            }
        }

        bookItemsData.postValue(bookItems)
        focusedBookItemData.postValue(changedBookItem)
    }

    private fun initSearchFlag() {
        isEndFlag = false
        page = 1
    }

    private fun addPage() {
        page++
    }

    private fun clearItems() {
        bookItemsData.postValue(emptyList())
    }

    fun showInitMessage() {
        if (bookItemsData.value?.isEmpty() == true) {
            //messageText.postValue(context.getString(R.string.descriptive_search_message))
            messageText.postValue("책 이름을 입력하면 자동으로\n검색이 됩니다.😽")
        }
    }

    fun showInitKeyboard() {
        if (bookItemsData.value?.isEmpty() == true) {
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