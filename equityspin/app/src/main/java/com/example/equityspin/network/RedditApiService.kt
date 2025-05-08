package com.example.equityspin.network

import com.example.equityspin.model.RedditResponse
import retrofit2.Response // Import Retrofit's Response wrapper for handling HTTP responses
import retrofit2.http.GET // Import the GET annotation to define a GET request

interface RedditApiService {
    // The URL path provided will be appended to the base URL defined in your Retrofit instance.
    @GET("wallstreetbets/top.json?t=day&limit=20")
    // It returns a Response-wrapped RedditResponse, which represents the API's JSON result.
    suspend fun getTopPosts(): Response<RedditResponse>
}