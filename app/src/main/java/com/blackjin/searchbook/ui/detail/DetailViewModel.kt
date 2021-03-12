package com.blackjin.searchbook.ui.detail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.blackjin.searchbook.ui.model.BookItem

//TODO check SavedStateHandle
//https://pluu.github.io/blog/android/2020/02/20/savedstatehandle/
class DetailViewModel(
    handle: SavedStateHandle
) : ViewModel() {

    val bookItem: BookItem = handle[DetailFragment.ARGUMENT_BOOK_ITEM] ?: BookItem()

    val isLike = MutableLiveData(bookItem.isLike)
    val changedBookItem = MediatorLiveData<BookItem>().apply {
        addSource(isLike) {
            postValue(bookItem.copy(isLike = it))
        }
    }

    fun changeLike() {
        val currentLike = isLike.value ?: false
        isLike.postValue(currentLike.not())
    }
}