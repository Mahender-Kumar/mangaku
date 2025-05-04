package com.example.mangaku.features.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController

@Composable
fun SignInScreen(viewModel: SignInViewModel, navController: NavHostController) {
    val email by viewModel.email
    val password by viewModel.password

    Column {
        TextField(value = email, onValueChange = { viewModel.email.value = it })
        TextField(value = password, onValueChange = { viewModel.password.value = it })
        Button(onClick = {
            viewModel.onSignIn {
                navController.navigate("home") {
                    popUpTo("sign_in") { inclusive = true }
                }
            }
        }) {
            Text("Sign In")
        }
    }
}
