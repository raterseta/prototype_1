package com.example.prototype_1

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    lateinit var googleSignInClient: GoogleSignInClient

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        configureGoogleSignIn(application)
        checkAuthState()
    }

    fun configureGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun checkAuthState() {
        val isAuthenticated = sharedPreferences.getBoolean("is_authenticated", false)
        _authState.value = if (isAuthenticated) AuthState.Authenticated else AuthState.Unauthenticated
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPreferences.edit().putBoolean("is_authenticated", true).apply()
                    _authState.value = AuthState.Authenticated

                    // Set the user's display name
                    _userName.value = account.displayName

                    onResult(true, null)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signOut(onSignOutComplete: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            sharedPreferences.edit().putBoolean("is_authenticated", false).apply()
            _authState.value = AuthState.Unauthenticated
            _userName.value = null  // Clear the user name on sign out
            onSignOutComplete()
        }
    }

    fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getBoolean("is_authenticated", false)
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}