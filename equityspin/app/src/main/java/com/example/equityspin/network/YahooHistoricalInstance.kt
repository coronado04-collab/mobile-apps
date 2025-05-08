package com.example.equityspin.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object YahooHistoricalInstance {
//    Base URL
    private const val BASE_URL = "https://yahoo-finance15.p.rapidapi.com/"
//    Retrofit client build
    val api: YahooHistoricalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // base url
            .addConverterFactory(GsonConverterFactory.create()) // GSON for JSON
            .build()
            .create(YahooHistoricalApiService::class.java) // use the YahooHistoricalApiService for the queries
    }
}