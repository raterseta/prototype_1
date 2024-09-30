package com.example.prototype_1

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        authViewModel.configureGoogleSignIn(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(key1 = Unit) {
                        if (authViewModel.isUserAuthenticated()) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    MyAppNavigation(authViewModel = authViewModel, activity = this, navController = navController)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    authViewModel.signInWithGoogle(it) { success, error ->
                        if (success) {
                            // Navigate to home screen
                            runOnUiThread {
                                setContent {
                                    MaterialTheme {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            color = MaterialTheme.colorScheme.background
                                        ) {
                                            val navController = rememberNavController()
                                            MyAppNavigation(authViewModel = authViewModel, activity = this, navController = navController)
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Show error message
                            // You might want to show a Toast or Snackbar here
                        }
                    }
                }
            } catch (e: ApiException) {
                // Handle sign in failed
                // You might want to show a Toast or Snackbar here
            }
        }
    }
}