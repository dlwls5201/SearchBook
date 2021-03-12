package com.blackjin.searchbook.ui.model

import android.os.Parcelable
import com.blackjin.searchbook.data.model.Document
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

@Parcelize
data class BookItem(
    val id: Int = -1,
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

        fun getUniqueId(name: String, isbn: String) = abs((name + isbn).hashCode())
    }

    override fun toString(): String {
        return "(id : $id, name : $name, isLike : $isLike)"
    }
}

fun List<Document>.mapToItem() = map { it.mapToItem() }

private fun Document.mapToItem() = BookItem(
    id = BookItem.getUniqueId(name = title ?: "", isbn = isbn ?: "1"),
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