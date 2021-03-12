package com.blackjin.searchbook.data.api

import com.blackjin.searchbook.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("v3/search/book")
    suspend fun searchBook(
        @Query("target") target: String,
        @Query("query") query: String,
        @Query("page") page: Int, //1~50, default: 1
        @Query("size") size: Int //1~50, default: 10
    ): SearchResponse
}