package com.example.equityspin.network

import com.example.equityspin.model.YahooFinanceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface YahooFinanceApiService {
    @Headers(
//        We are using rapid api here, requires the api key and host (yfinance host)
//        API key is exposed for simplicity.
        "x-rapidapi-key: 68981f315amsh37dd65c572b18c9p1ae890jsn9bb2e54e9913",
        "x-rapidapi-host: yahoo-finance15.p.rapidapi.com"
    )
//    With this we get the endpoint for the stock quotes
    @GET("api/v1/markets/stock/quotes")
    suspend fun getStockQuotes(
//        This query allows one to pass a ticker (the stock quote we want)
        @Query("ticker") tickers: String
    ): Response<YahooFinanceResponse>
}
