package com.example.equityspin.model

import com.google.gson.annotations.SerializedName

// Full historical data response from yahoo finance api
data class YahooHistoricalResponse(
    val meta: Meta, // Renamed to yahooMeta
    val body: Map<String, Item>
)


// Meta data of the symbol and content.
data class Meta( // Renamed to YahooMeta
    val currency: String,
    val symbol: String,
    val exchangeName: String,
    val instrumentType: String,
    @SerializedName("firstTradeDate") val firstTradeDate: Long,
    @SerializedName("regularMarketTime") val regularMarketTime: Long,
    val gmtoffset: Int,
    val timezone: String,
    val exchangeTimezoneName: String,
    val regularMarketPrice: Double,
    val chartPreviousClose: Double,
    val previousClose: Double,
    val scale: Int,
    val priceHint: Int,
    val dataGranularity: String,
    val range: String
)


// Represents a single historical price record for a specific date.
data class Item(
    val date: String,
    @SerializedName("date_utc") val dateUtc: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)