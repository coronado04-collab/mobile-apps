package com.example.equityspin.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Building the retrofit client
//Setting the base url, uses Gson as a JSON converter
// Uses the YahooFinanceApiService to do the API contract
object YahooFinanceInstance {

//    yahoo finance api base url to use
    private const val BASE_URL = "https://yahoo-finance15.p.rapidapi.com/"
    val api: YahooFinanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
//            Using GSON for JSON files (yfinance)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YahooFinanceApiService::class.java)
    }
}