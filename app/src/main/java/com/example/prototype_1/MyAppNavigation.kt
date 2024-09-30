package com.example.prototype_1

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prototype_1.pages.HomeActivity
import com.example.prototype_1.pages.LoginActivity

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    activity: MainActivity,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            LoginActivity(navController = navController, authViewModel = authViewModel, activity = activity)
        }
        composable("home") {
            HomeActivity(navController = navController, authViewModel = authViewModel)
        }
    }
}