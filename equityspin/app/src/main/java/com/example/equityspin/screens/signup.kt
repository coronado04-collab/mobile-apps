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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.equityspin.navigation.AppScreens
import com.example.equityspin.R
import com.example.equityspin.MyViewModel


@Composable
fun SignUpScreen(viewModel: MyViewModel) {

    // Sign up using login, password, confirm the password and show dialog.
    // State variables to store user input and dialog visibility
    var login by rememberSaveable {  mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPasswd by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }

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
            Column {
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))
//                 Title and subtitle
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.sign_in_to_continue),
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
//              User name
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = {
                        Text("Username") // Or use stringResource if internationalized
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

//                Email input
                TextField(
                    value = login,
                    onValueChange = { login = it },
                    placeholder = {
                        Text(stringResource(R.string.email_edit_text))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
//                Password input
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(stringResource(R.string.password_edit_text))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
//                Confirm password input
                TextField(
                    value = confirmPasswd,
                    onValueChange = { confirmPasswd = it },
                    placeholder = {
                        Text(stringResource(R.string.confirm_edit_text))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
//                Sign up button with password check
                Button(
                    onClick = {
                        if (password != confirmPasswd) {
                            showDialog = true
                        } else {
                            viewModel.signUp(login, password, username)
                        }
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.signup_label))
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                            contentDescription = stringResource(R.string.signup_label),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
//            Bottom text button to navigate to login screen
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxHeight(0.2f)
            ) {
                TextButton(
                    onClick = { viewModel.navigate(AppScreens.Login.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.already_account))
                }
            }
        }
        Spacer(
            modifier = Modifier
                .weight(0.15f)
                .fillMaxHeight()
        )
//        Pass words dont match, give an error
        if (showDialog) {
            DisplayDialog(
                title = stringResource(R.string.error),
                errorMessage = stringResource(R.string.password_does_not_match),
                onDismiss = { showDialog = false }
            )
        }
    }
}



//This is the alert dialog shown for validation errors
@Composable
fun DisplayDialog(
    title: String, // Title text to display
    errorMessage: String, // the message to show from the error
    onDismiss: () -> Unit
) {
    AlertDialog(
//        Triggered when the user dismisses the dialog
        onDismissRequest = onDismiss,
//        title and text of the dialog
        title = { Text(text = title) },
        text = { Text(text = errorMessage) },

//        Confirmation button
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
}