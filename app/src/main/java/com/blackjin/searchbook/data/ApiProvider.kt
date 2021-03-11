package com.blackjin.searchbook.data

import com.blackjin.searchbook.BuildConfig
import com.blackjin.searchbook.data.source.remote.SearchApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiProvider {

    private const val baseUrl = "https://dapi.kakao.com/"

    fun provideSearchApi(): SearchApi = getRetrofitBuild().create(SearchApi::class.java)

    private fun getRetrofitBuild() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(getOkhttpClient())
        .addConverterFactory(getGsonConverter())
        .build()

    private fun getGsonConverter() = GsonConverterFactory.create()

    private fun getOkhttpClient() = OkHttpClient.Builder().apply {
        readTimeout(60, TimeUnit.SECONDS)
        connectTimeout(60, TimeUnit.SECONDS)
        writeTimeout(5, TimeUnit.SECONDS)

        addInterceptor(getLoggingInterceptor())
        addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", BuildConfig.KAKAO_REST_API_KEY)
                    .build()
            )
        }
    }.build()

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
}