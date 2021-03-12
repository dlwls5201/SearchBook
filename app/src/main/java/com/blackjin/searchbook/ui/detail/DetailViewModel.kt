package com.blackjin.searchbook.ui.detail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.blackjin.searchbook.ui.model.BookItem

//https://pluu.github.io/blog/android/2020/02/20/savedstatehandle/
class DetailViewModel(
    handle: SavedStateHandle
) : ViewModel() {

    val bookItem: BookItem = handle[DetailFragment.ARGUMENT_BOOK_ITEM] ?: BookItem()

    val eventLike = MutableLiveData(bookItem.isLike)
    val eventChangedBookItem = MediatorLiveData<BookItem>().apply {
        addSource(eventLike) {
            postValue(bookItem.copy(isLike = it))
        }
    }

    fun changeLike() {
        val currentLike = eventLike.value ?: false
        eventLike.postValue(currentLike.not())
    }
}