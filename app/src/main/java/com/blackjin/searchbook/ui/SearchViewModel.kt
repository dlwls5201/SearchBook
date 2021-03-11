package com.blackjin.searchbook.ui

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackjin.searchbook.R
import com.blackjin.searchbook.data.repository.SearchBookRepositoryImpl
import com.blackjin.searchbook.ui.model.BookItem
import com.blackjin.searchbook.utils.Dlog
import com.example.toyproject.data.base.BaseResponse
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchBookRepositoryImpl
) : ViewModel() {

    val focusedBookItem = MutableLiveData<BookItem>()

    val items = MutableLiveData<List<BookItem>>(emptyList())

    fun showBookItem(bookItem: BookItem) {
        focusedBookItem.postValue(bookItem)
    }

    fun searchRepository(context: Context) {
        hideKeyboard()

        viewModelScope.launch {
            val query = editSearchText.value ?: return@launch
            Dlog.d("query : $query")

            searchRepository.searchBook(query, object : BaseResponse<Pair<Int, List<BookItem>>> {
                override fun onSuccess(data: Pair<Int, List<BookItem>>) {
                    val (totalCount, books) = data
                    if (totalCount == 0) {
                        clearItems()
                        showMessage(context.getString(R.string.not_search_result))
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

    private fun clearItems() {
        items.postValue(emptyList())
    }

    val isLoading = MutableLiveData(false)

    val isKeyboard = MutableLiveData(true)

    val messageText = MutableLiveData("")

    val isVisibleMessageText = MediatorLiveData<Boolean>().apply {
        addSource(messageText) { message ->
            postValue(TextUtils.isEmpty(message).not())
        }
    }

    val editSearchText = MutableLiveData("")

    val enableSearchButton = MediatorLiveData<Boolean>().apply {
        addSource(editSearchText) { query ->
            if (query.isNullOrEmpty()) {
                postValue(false)
            } else {
                postValue(true)
            }
        }
    }

    fun showInitMessage(context: Context) {
        if (items.value?.isEmpty() == true) {
            messageText.postValue(context.getString(R.string.descriptive_search_message))
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