package com.example.equityspin
import com.example.equityspin.screens.PortfolioScreen
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.equityspin.navigation.AppScreens
import com.example.equityspin.screens.LoginScreen
import com.example.equityspin.screens.ProfileScreen
import com.example.equityspin.screens.NewsScreen
import com.example.equityspin.screens.SignUpScreen
import com.example.equityspin.ui.theme.CustomTopAppBar
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable // Apps scaffold and naviation
fun MainScreen(viewModel: MyViewModel = viewModel()) {
//    navigation controller and state flows
    val navController = rememberNavController()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val routeState by viewModel.route.collectAsState()
    val context = LocalContext.current

    // Make isUserLoggedIn reactive
    var isUserLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

//Setting up the app layout (top, bottom bar and content)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CustomTopAppBar("EquitySpin") },
        bottomBar = {
            if (isUserLoggedIn) { // Only show the bottom bar if the user is logged in
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
//        Handles composable destinations based on navigation route
        NavHost(
            navController = navController,
            startDestination = if (isUserLoggedIn) AppScreens.Portfolio.route else AppScreens.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
//            These are all the navigation routes and their associated composable screens
            composable(AppScreens.Portfolio.route) { PortfolioScreen(viewModel = viewModel) }
            composable(AppScreens.News.route) { NewsScreen(viewModel = viewModel) }
            composable(AppScreens.Profile.route) { ProfileScreen(navController, viewModel = viewModel) }
            composable(AppScreens.Login.route) { LoginScreen(viewModel) }
            composable(AppScreens.SignUp.route) { SignUpScreen(viewModel) }
        }
    }
// Toast message is updated in the viewModel
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.showToast(null)
        }
    }
//Navigate to the new route if routeState is set in ViewModel.
    LaunchedEffect(routeState) {
        routeState?.let { route ->
            navController.navigate(route)
            viewModel.navigate(null)
        }
    }
// Observe FirebaseAuth to update isUserLoggedIn when auth state changes
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            isUserLoggedIn = auth.currentUser != null
        }
    }

}

// This is the bottom navigation bar for switching between main screens when logged in
@Composable
fun BottomNavigationBar(navController: NavController) {
//     Current navigation route
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

//    Render navigation bar with icons and labels for each screen
    NavigationBar {
        AppScreens.items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
//                    Navigate to screen and avoid multiple copies of the same destination.
                    navController.navigate(screen.route) {
                        popUpTo(AppScreens.Portfolio.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                label = { Text(text = screen.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondary // Optional highlight
                )
            )
        }
    }
}

