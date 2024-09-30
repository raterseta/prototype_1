package com.example.prototype_1.pages

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prototype_1.AuthViewModel
import com.example.prototype_1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginActivity(
    navController: NavController,
    authViewModel: AuthViewModel,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val purpleColor = Color(0xFF8B4F99)
    val Montserrat = FontFamily(Font(R.font.montserrat_regular))
    val MontserratBold = FontFamily(Font(R.font.montserrat_bold))

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.onenote_logo), // Replace with your Google logo
                    contentDescription = "Google Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(screenWidth * 0.2f)
                        .height(screenWidth * 0.2f)
                )
                Spacer(modifier = Modifier.width(35.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Google Sign-In",
                        fontWeight = FontWeight.Bold,
                        fontFamily = MontserratBold,
                        color = purpleColor,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Log in to your account to\naccess the app",
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        color = purpleColor,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val signInIntent = authViewModel.googleSignInClient.signInIntent
                    activity.startActivityForResult(signInIntent, 100)
                },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(30.dp),
                        clip = false
                    )
            ) {
                Text(
                    text = "Sign in with Google",
                    style = TextStyle(color = Color.Black, fontSize = 16.sp, fontFamily = MontserratBold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Need help?",
                fontFamily = MontserratBold,
                color = purpleColor
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}