package com.example.equityspin.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import com.example.equityspin.navigation.AppScreens
import com.example.equityspin.R
import com.example.equityspin.MyViewModel


// Jetpack composable for the login screen
@Composable
fun LoginScreen(viewModel: MyViewModel) {
//    both are saveable which means that we can save their state (login and password can be saved after logging n)
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

//    Boxes used to create the login page.
    Row(modifier = Modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxHeight()
        )
        Box(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
        ) {
//            Login page
            Column {
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                Text(
                    text = stringResource(R.string.login_label),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
//                Have a text that says please sign in to continue
                Text(
                    text = stringResource(R.string.sign_in_to_continue),
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
//                Here we have the value for login (the email)
                TextField(
                    value = login,
                    onValueChange = { login = it },
                    placeholder = {
                        Text(stringResource(R.string.email_edit_text))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
//                Here we can type in our password
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(stringResource(R.string.password_edit_text))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
//                When we click on the login button, we use the viewModel to handle the backend
//                With this we save the mutable state for the login and password
                Button(
                    onClick = {
                        viewModel.login(login, password)
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        This is how the button looks for logging in. We use an arrow from drawables
                        Text(stringResource(R.string.login_label))
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = stringResource(R.string.login_label),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxHeight(0.2f)
            ) {
//                This is a button that allows us to sign up. When clicked on it, proceeds to the SignUp page
//                SignUp is on the AppScreens, which then points to our signup.kt
                TextButton(
                    onClick = { viewModel.navigate(AppScreens.SignUp.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.don_t_have_an_account_sign_up))
                }
            }
        }
        Spacer(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxHeight()
        )
    }
}




















//package com.example.equityspin.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.equityspin.navigation.AppScreens
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import com.google.firebase.auth.FirebaseAuth
//
//
//@Composable
//fun LoginScreen(
//    navController: NavController,
//    modifier: Modifier = Modifier
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    var isLoading by remember { mutableStateOf(false) }
//
//    val auth = FirebaseAuth.getInstance()
//
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(24.dp)
//            .background(Color.White),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    )
//
//    {
//        Text("Welcome", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Show error message if any
//        errorMessage?.let {
//            Text(
//                text = it,
//                color = Color.Red,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//
//        Button(
//            onClick = {
//                isLoading = true
//                errorMessage = null
//
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        isLoading = false
//                        if (task.isSuccessful) {
//                            navController.navigate(AppScreens.Profile.route)
//                        } else {
//                            errorMessage = task.exception?.message ?: "Login failed"
//                        }
//                    }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading
//        ) {
//            Text(if (isLoading) "Logging in..." else "Login")
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextButton(
//            onClick = {
//                navController.navigate(AppScreens.SignUp.route)
//            }
//        ) {
//            Text("Don't have an account? Sign up")
//        }
//    }
//}
