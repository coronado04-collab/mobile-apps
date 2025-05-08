package com.example.equityspin.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RedditInstance {
//    Base URL for reddit, it will have queries appended to it.
    private const val BASE_URL = "https://www.reddit.com/r/"
//    Only created when it is first accessed (lazy)
    val api: RedditApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Uses Gson for JSON
            .build()
            .create(RedditApiService::class.java) // Create an implementation using Retrofit
    }
}