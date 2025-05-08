package com.example.equityspin

// Data class representing an owned asset
data class Asset(
    val symbol: String, // Asset symbol
    val quantity: Double, // Quantity we have
    val purchasePrice: Double, // The purchase price (bought at)
    val currentPrice: Double = 0.0 // Current market price
) {
    // The purchase value is the quantity we have * what we purchased at
    val purchaseValue: Double get() = quantity * purchasePrice
    // current value is the volume owned * current market price.
    val currentValue: Double get() = quantity * currentPrice
}
