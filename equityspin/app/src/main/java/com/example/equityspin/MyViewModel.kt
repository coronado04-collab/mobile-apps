package com.example.equityspin

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.equityspin.model.YahooFinanceResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.equityspin.navigation.AppScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.equityspin.network.RedditInstance
import com.example.equityspin.network.YahooFinanceInstance
import com.example.equityspin.network.YahooHistoricalInstance
import com.example.equityspin.screens.NewsArticle
import com.github.mikephil.charting.data.Entry
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// This handles all the backend part of the app
// business logic and states.
class MyViewModel(application: Application) : AndroidViewModel(application) {

    //     Toast messages trigger UI messages
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage
    // Firebase Firestore and Auth instances for backend
    private val firestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth


    // Used to trigger navigation events from ViewModel to UI
    private val _route = MutableStateFlow<String?>(null)
    val route: StateFlow<String?> get() = _route

    // Holds the list of news articles retrieved from Reddit
    private val _redditNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val redditNews: StateFlow<List<NewsArticle>> get() = _redditNews

    // List of assets owned by the user
    private val _ownedAssets = MutableStateFlow<List<Asset>>(emptyList())
    val ownedAssets: StateFlow<List<Asset>> = _ownedAssets.asStateFlow()


    // Current holdings of the user
    private val _currentHoldings = MutableStateFlow(0.0)
    val currentHoldings: StateFlow<Double> = _currentHoldings.asStateFlow()

    // Chart entries
    private val _chartEntries = MutableStateFlow<List<Entry>>(emptyList())
    val chartEntries: StateFlow<List<Entry>> = _chartEntries

//    Member since
    private val _memberSince = MutableStateFlow<String?>(null)
    val memberSince: StateFlow<String?> get() = _memberSince

//  Username
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> get() = _username



    // Utility function to show a toast message
    fun showToast(message: String?) {
        _toastMessage.value = message
    }
    // Utility function to trigger navigation in the UI.
    fun navigate(route: String?) {
        _route.value = route
    }

    // Handles user registration using FireBase Auth
//    fun signUp(email: String, password: String) {
//        viewModelScope.launch {
//            try {
//                // Create the user / password
//                auth.createUserWithEmailAndPassword(email, password).await()
//                _route.value = AppScreens.Portfolio.route
//
//            } catch (e: Exception) {
//                // Error message as toast
//                _toastMessage.value = e.message
//            }
//        }
//    }

    fun signUp(email: String, password: String, username: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                val memberSince = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())


                uid?.let {
                    val userData = hashMapOf(
                        "email" to email,
                        "username" to username,
                        "memberSince" to memberSince
                    )
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .set(userData)
                        .await()
                }

                _route.value = AppScreens.Portfolio.route
            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }

    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

                // Get the memberSince value or set a default if not found
                val memberDate = doc.getString("memberSince") ?: "Unknown"
                _memberSince.value = memberDate // Updating the StateFlow

                val name = doc.getString("username") ?: "Usuario"
                _username.value = name

            } catch (e: Exception) {
                _memberSince.value = "Unknown" // Handle error and set fallback value
                _username.value = "Usuario"
            }
        }
    }


    // Handles login using FireBase Authentication
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                // Asynchonously sign in (checked the combination works of email/password)
                auth.signInWithEmailAndPassword(email, password).await()
                loadHoldings() // Fetch user holdings after login

                // Navigate to the portfolio after logging in
                _route.value = AppScreens.Portfolio.route

            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }
    // Handles user logout and redirects to login screen
    fun logout() {
        viewModelScope.launch {
            try {
                // Navigate to the Login after signing out
                auth.signOut()
                _route.value = AppScreens.Login.route
            // Error if it fails (from the try)
            } catch (e: Exception) {
                _toastMessage.value = e.message
            }
        }
    }


    // Fetch the top posts from the r/wallstreetbets subreddit
    fun fetchTopWallStreetBetsPosts() {
        viewModelScope.launch {
            try {
                // Make the API call
                val response = RedditInstance.api.getTopPosts()

                // Check if the request was successful
                if (response.isSuccessful) {
                    // Extract the posts and map them to NewsArticle with just the title
                    val posts = response.body()?.data?.children?.map {
                        NewsArticle(
                            title = it.data.title,
                            description = it.data.selftext,
                            link = "https://www.reddit.com${it.data.permalink}"
                        )
                    } ?: emptyList()

                    // Update the UI with the list of NewsArticle
                    _redditNews.value = posts
                } else {
                    showToast("Failed to load Reddit posts")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }
    // Inputs from UI for symbobl and quantity
    val inputSymbol = MutableStateFlow("")
    val inputQuantity = MutableStateFlow("")
    // Load the holdings
    init {
        loadHoldings()
    }

    // Load user holdings from Firestore
    private fun loadHoldings() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch  // Obtain the UID of logged in user
            val snapshot = firestore.collection("users") // Access the collection 'users'
                .document(uid)  // Use the UID like the ID in the document
                .collection("holdings")  // Subcollection 'holdings' inside the user.
                .get()
                .await()
            // Map the documents of holdings to a list of assets
            val assets = snapshot.documents.mapNotNull { doc ->
                val symbol = doc.id
                val qty = doc.getDouble("quantity") ?: return@mapNotNull null
                val price = doc.getDouble("purchasePrice") ?: return@mapNotNull null
                Asset(symbol, qty, price)
            }
            _ownedAssets.value = assets // Update the list of active assets
            refreshPrices() // Update the prices of our assets owned
        }
    }

    // Buy asset using Yahoo Finance API
    fun buyAsset() {
        viewModelScope.launch {
            val symbol = inputSymbol.value.trim().uppercase()
            val qty = inputQuantity.value.toDoubleOrNull() ?: return@launch

            try {
                // Obtain the current price of the stocks using Yahoo API
                val response = YahooFinanceInstance.api.getStockQuotes(symbol)
                if (!response.isSuccessful) {
                    Log.e("BUY_ASSET_ERROR", "API call failed: ${response.code()} ${response.message()}")
                    showToast("Failed to fetch price.")
                    return@launch
                }
                // Get the quote and add error handling for no quote
                val quote = response.body()?.body?.firstOrNull()
                if (quote == null) {
                    Log.e("BUY_ASSET_ERROR", "No quote returned for $symbol")
                    showToast("No quote found for $symbol.")
                    return@launch
                }

                val price = quote.regularMarketPrice

                // Save the stock information and current state to the Firestore.
                val uid = auth.currentUser?.uid ?: return@launch  // Obtain the UID of logged in user
                val docRef = firestore.collection("users")  // Access the user's collection
                    .document(uid) // Use the UID like the ID in the document
                    .collection("holdings")  //Subcollection holdings inside the 'user' document
                    .document(symbol)  // Each holding is saved with its symbol

                val snapshot = docRef.get().await()
                val existingQty = snapshot.getDouble("quantity") ?: 0.0
                val avgPurchasePrice = snapshot.getDouble("purchasePrice") ?: price
                val totalQty = existingQty + qty
                val totalCost = existingQty * avgPurchasePrice + qty * price
                val newAvgPrice = totalCost / totalQty
                // Setting quantity and purchase price
                docRef.set(
                    mapOf(
                        "quantity" to totalQty,
                        "purchasePrice" to newAvgPrice
                    )
                ).await()

                // Notify the purchase was successful.
                val notificationHelper = NotificationHelper(getApplication())
                notificationHelper.statusBarNotification(
                    "Completed $symbol Stocks",
                    "You have successfully purchased $qty shares of $symbol at $price each."
                )
                // Reload holdings and clean the values used for the symbol and quantity
                loadHoldings()
                inputSymbol.value = ""
                inputQuantity.value = ""

            } catch (e: Exception) {
                Log.e("BUY_ASSET_ERROR", "Failed to buy asset (${symbol})", e)
                showToast("Error: ${e.message}")
            }
        }
    }



    // Selling the asset function
    fun sellAsset(symbol: String, sellQty: Double) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch // User is logged in
            val docRef = firestore.collection("users")
                .document(uid)
                .collection("holdings") // Getting the holdings
                .document(symbol) // symbol from holdings

            try {
                val snapshot = docRef.get().await() //fetch current holdings
                val currentQty = snapshot.getDouble("quantity") ?: 0.0

                if (sellQty <= 0 || sellQty > currentQty) { // Validate sale (qty>0 and <= current qty)
                    showToast("Invalid quantity to sell.")
                    return@launch
                }

                val newQty = currentQty - sellQty

                if (newQty == 0.0) {
                    docRef.delete().await() // Remove document if all shares are sold
                } else {
                    docRef.update("quantity", newQty).await() // Update with reduced quantity
                }

                loadHoldings() // Refresh holdings list
                // notification for selling in the notifications of android
                val notificationHelper = NotificationHelper(getApplication())
                notificationHelper.statusBarNotification(
                    "Sold $symbol Stocks",
                    "You have sold $sellQty shares of $symbol."
                )
            } catch (e: Exception) {
                Log.e("SELL_ASSET_ERROR", "Failed to sell asset ($symbol)", e)
                showToast("Error: ${e.message}")
            }
        }
    }

    // Refresh all asset prices using Yahoo API
    private fun refreshPrices() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch  // Obtén el UID del usuario logueado
            val updated = _ownedAssets.value.map { asset ->
                try {
                    // Obtain the current price of the stock quote
                    val response: YahooFinanceResponse? = try {
                        YahooFinanceInstance.api.getStockQuotes(asset.symbol).body()
                    } catch (e: Exception) {
                        Log.e("REFRESH_PRICE_ERROR", "Failed to fetch price for ${asset.symbol}", e)
                        null
                    }

                    // If we were able to get a price then we update it, else just leave it as is.
                    val current = response?.body?.firstOrNull()?.regularMarketPrice ?: asset.purchasePrice

                    //  Create a copy of the current price with the new price.
                    asset.copy(currentPrice = current)
                } catch (e: Exception) {
                    Log.e("REFRESH_PRICE_ERROR", "Error refreshing price for ${asset.symbol}", e)
                    asset  // if error, then we maintain the previous price
                }
            }
            _ownedAssets.value = updated  // Update the list of the held stocks with the new price
            _currentHoldings.value = updated.sumOf { it.currentValue }  // Calculate the total value of the holdings (sum)
        }
    }
    // Getting the historical data for the line chart
    fun fetchHistorical(symbol: String) {

        viewModelScope.launch(Dispatchers.IO) {

            try {
                // Make a network request to get weekly historical data for the given stock symbol
                val resp = YahooHistoricalInstance.api.getHistoricalData(symbol = symbol, interval = "1wk")
                // Log the raw HTTP response for debugging purposes
                Log.d("Historical", "Response: $resp")
                Log.d("HistoricalBody", "Raw Response Body: ${resp.body()?.toString()}")

                // Check if the response was not successful
                if (!resp.isSuccessful) {
                    // Log the error code and message
                    Log.e("HistoricalError", "API Error: ${resp.code()} - ${resp.errorBody()?.string()}")
                    return@launch // Exit early if API call failed
                }
                // Extract the response body or return early if Null.
                val body = resp.body() ?: return@launch
                val entries = body.body.entries.mapNotNull { (timestamp, item) ->
                    Entry(item.dateUtc.toFloat(), item.close.toFloat()) // Use dateUtc for X-axis
                }.sortedBy { it.x } // Sort entries by date

                _chartEntries.value = entries

            } catch (e: Exception) {
                // Catch and log any unexpected error
                Log.e("HistoricalError", "Exception during API call: ${e.localizedMessage}")
            }
        }
    }




}