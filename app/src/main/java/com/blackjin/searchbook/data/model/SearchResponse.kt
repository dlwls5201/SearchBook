package com.blackjin.searchbook.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("documents")
    val documents: List<Document> = emptyList(),
    @SerializedName("meta")
    val meta: Meta? = null
)