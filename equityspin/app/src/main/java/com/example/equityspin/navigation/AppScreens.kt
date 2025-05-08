package com.example.equityspin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


// Main AppScreens.
// Sealed class to fix the types and the screens
sealed class AppScreens(val route: String, val label: String,  val icon: ImageVector) {
    data object Portfolio : AppScreens("portfolio", "Portfolio", Icons.Default.Home)
    data object News : AppScreens("news", "News", Icons.Default.Info)
    data object Profile : AppScreens("profile", "Profile", Icons.Default.Person)
    data object Login : AppScreens("login", "Login", Icons.Default.Person)
    data object SignUp : AppScreens("signup", "SignUp", Icons.Default.Person)

//    These are our bottom screens (the active ones we can interact with)
    companion object {
        val items = listOf(Portfolio, News, Profile)
    }
}