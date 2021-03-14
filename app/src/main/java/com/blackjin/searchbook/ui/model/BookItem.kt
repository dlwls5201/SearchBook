package com.blackjin.searchbook.ui.model

import android.os.Parcelable
import com.blackjin.searchbook.data.model.Document
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookItem(
    val id: String = "",
    val totalCount: Int = 0,
    val thumbnail: String = "",
    val name: String = "",
    val description: String = "",
    val publisher: String = "",
    val price: Int = 0,
    val date: String = "",
    val isLike: Boolean = false
) : Parcelable {

    companion object {

        private const val MULTIPLE_ISBN_DIVIDER = " "

        fun getUniqueId(isbn: String): String {
            return if (isMultipleISBN(isbn)) {
                isbn.split(MULTIPLE_ISBN_DIVIDER).first()
            } else {
                isbn
            }
        }

        private fun isMultipleISBN(isbn: String) = isbn.contains(MULTIPLE_ISBN_DIVIDER)
    }

    override fun toString(): String {
        return "(id : $id, name : $name, isLike : $isLike)"
    }
}

fun List<Document>.mapToItem() = map { it.mapToItem() }

private fun Document.mapToItem() = BookItem(
    id = BookItem.getUniqueId(isbn ?: ""),
    thumbnail = thumbnail ?: "",
    name = title ?: "",
    description = contents ?: "",
    publisher = publisher ?: "",
    price = price ?: 0,
    date = divideDateTimeFromT(datetime)
)

private fun divideDateTimeFromT(date: String?): String {
    val firstDate = date?.split("T")?.firstOrNull()
    return firstDate ?: ""
}