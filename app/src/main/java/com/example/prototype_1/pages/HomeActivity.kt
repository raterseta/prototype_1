package com.example.prototype_1.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prototype_1.AuthViewModel

@Composable
fun HomeActivity(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Home Page")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.signOut {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }) {
            Text(text = "Sign Out")
        }
    }
}