package com.example.equityspin.network

import com.example.equityspin.model.YahooHistoricalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// ApiService for what we can choose from.
// This is to get historical data to display on the portfolio screen
interface YahooHistoricalApiService {
    @Headers(
        "x-rapidapi-key: 6f78559224msh314fd227e3bba20p187111jsn631f6bf3971f", // Replace with your actual API key
        "x-rapidapi-host: yahoo-finance15.p.rapidapi.com"
    )
    // Defines a GET request to this endpoint
    @GET("api/v1/markets/stock/history")
    suspend fun getHistoricalData(
        // Required stock ticker
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1wk", // 1 week default
        // Whether to include dividend/split adjustments (defaulted to False, we only want price)
        @Query("diffandsplits") diffandsplits: Boolean = false
    ): Response<YahooHistoricalResponse>
}