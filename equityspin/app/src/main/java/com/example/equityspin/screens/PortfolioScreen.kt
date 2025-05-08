package com.example.equityspin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.equityspin.Asset
import com.example.equityspin.MyViewModel
import com.example.equityspin.R
import com.example.equityspin.ui.StockLineChart
import com.github.mikephil.charting.data.Entry

//Jetpack composable for the Portfolio Screen
@Composable
fun PortfolioScreen(viewModel: MyViewModel, modifier: Modifier = Modifier) {
    //    Observing the state from the viewModel (backend)
    val ownedAssets by viewModel.ownedAssets.collectAsState()
    val totalHoldings by viewModel.currentHoldings.collectAsState()
    val chartEntries by viewModel.chartEntries.collectAsState()

    // Inputs for buy/sell
    val symbolInput by viewModel.inputSymbol.collectAsState()
    val qtyInput by viewModel.inputQuantity.collectAsState()
    val isLoading = remember { mutableStateOf(false) }

    // Chart symbol state
    var chartSymbol by remember { mutableStateOf("AAPL") }

    // Gets our totalHoldings from the viewModel and displays it above the page.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //        Gets our totalHoldings from the viewModel and displays it above the page.
        Text(
            text = stringResource(R.string.total_holdings, totalHoldings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //            This is a box that allows one to input a symbol for buying
            OutlinedTextField(
                value = symbolInput,
                onValueChange = { viewModel.inputSymbol.value = it },
                label = { Text(stringResource(R.string.symbol)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_symbol_desc)) },
                modifier = Modifier.weight(1f)
            )
            //            This is the amount of stock (volume) we want to buy
            OutlinedTextField(
                value = qtyInput,
                onValueChange = { viewModel.inputQuantity.value = it },
                label = { Text(stringResource(R.string.quantity)) },
                singleLine = true,
                modifier = Modifier.weight(0.7f)
            )
            //            Load the buyAsset in the viewModel upon click
            Button(
                onClick = {
                    isLoading.value = true
                    viewModel.buyAsset()
                    isLoading.value = false
                },
                enabled = symbolInput.isNotBlank() && qtyInput.toDoubleOrNull() != null && !isLoading.value,
                modifier = Modifier.weight(0.6f)
            ) {
                Text(
                    if (isLoading.value) stringResource(R.string.buying)
                    else stringResource(R.string.buy)
                )
            }
        }
//         Getting the historical data
        // Chart Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chartSymbol,
                onValueChange = { chartSymbol = it.uppercase() },
                label = { Text(stringResource(R.string.chart_symbol)) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_chart_symbol_desc)) },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { viewModel.fetchHistorical(chartSymbol) }) {
                Text(stringResource(R.string.load_chart))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            if (chartEntries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_chart_data, chartSymbol))
                }
            } else {
                StockLineChart(
                    entries = chartEntries,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        // List of holdings
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(ownedAssets) { asset ->
                AssetItem(asset = asset, onSell = { qtyToSell ->
                    viewModel.sellAsset(asset.symbol, qtyToSell)
                })
            }
        }
    }
}

@Composable
fun AssetItem(asset: Asset, onSell: (Double) -> Unit) {
    var sellQuantity by remember { mutableStateOf("") }
    var showSellDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) { // Info Column (the amount owned, the price purchased at, and the actual value)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = asset.symbol, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.owned, asset.quantity)
                )
                Text(
                    text = stringResource(R.string.purchase_price, asset.purchasePrice),
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string.actual_value, asset.currentPrice),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.total_value, asset.currentValue),
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = { showSellDialog = true }, modifier = Modifier.padding(top = 8.dp)) {
                    Text(stringResource(R.string.sell))
                }
            }
        }
    }
    // sell dialog when user taps the sell button
    if (showSellDialog) {
        AlertDialog(
            onDismissRequest = { showSellDialog = false },
            title = { Text(stringResource(R.string.sell_title, asset.symbol)) },
            text = {
                OutlinedTextField(
                    value = sellQuantity,
                    onValueChange = { sellQuantity = it },
                    label = { Text(stringResource(R.string.quantity_to_sell)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            // Confirm the sale
            confirmButton = {
                Button(
                    onClick = {
                        val qty = sellQuantity.toDoubleOrNull()
                        // Ensure the input is valid: a number>0 and <= quantity owned
                        if (qty != null && qty > 0 && qty <= asset.quantity) {
                            onSell(qty)
                            sellQuantity = ""
                            showSellDialog = false
                        }
                    },
                    // Enable button only when input is valid quantity
                    enabled = sellQuantity.toDoubleOrNull()?.let { it > 0 && it <= asset.quantity } == true
                ) {
                    Text(stringResource(R.string.sell))
                }
            },
            // button to cancel and close the dialog without any action
            dismissButton = {
                Button(onClick = { showSellDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}