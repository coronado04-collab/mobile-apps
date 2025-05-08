package com.example.equityspin.model

// Full response from the Yahoo Finance API
data class YahooFinanceResponse(
    val meta: MetaYahoo,
    val body: List<StockQuote>
)

// Meta data from yahoo. Symbol, version, ...
data class MetaYahoo(
    val version: String,
    val status: Int,
    val symbol: String,
    val processedTime: String
)

// Data of the stock. Stock symbol and prices.
data class StockQuote(
    val displayName: String,
    val symbol: String,
    val regularMarketPrice: Double,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double
)
