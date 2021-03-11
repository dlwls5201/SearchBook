package com.blackjin.searchbook.ui.model

import android.os.Parcelable
import com.blackjin.searchbook.data.model.Document
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookItem(
    val totalCount: Int = 0,
    val thumbnail: String = "",
    val name: String = "",
    val description: String = "",
    val publisher: String = "",
    val price: Int = 0,
    val date: String = "",
    val isLike: Boolean = false
) : Parcelable

fun List<Document>.mapToItem() = map { it.mapToItem() }

private fun Document.mapToItem() = BookItem(
    thumbnail = thumbnail ?: "",
    name = title ?: "",
    description = contents ?: "",
    publisher = publisher ?: "",
    price = price ?: 0,
    date = divideDataTimeFromT(datetime)
)

private fun divideDataTimeFromT(date: String?): String {
    val firstDate = date?.split("T")?.firstOrNull()
    return firstDate ?: ""
}