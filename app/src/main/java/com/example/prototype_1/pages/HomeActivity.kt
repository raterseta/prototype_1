//Saat foto sudah di simpan, saya ingin ditampilkan di halaman Home

package com.example.prototype_1.pages

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.prototype_1.AuthViewModel
import com.example.prototype_1.MainViewModel
import com.example.prototype_1.Model.ProfileData
import com.example.prototype_1.Model.ProfileLibrary
import com.example.prototype_1.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun HomeActivity(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val userName = authViewModel.userName.observeAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text(text = "+", style = MaterialTheme.typography.headlineMedium)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Home Page",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            userName.value?.let { name ->
                Text(text = "Signed in as $name")
            } ?: Text(text = "Signed in as Guest")

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

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumnAbs()
        }
    }
}

//@Composable
//fun LazyColumnAbs(
//    modifier: Modifier = Modifier,
//    viewModel: MainViewModel = viewModel(factory = ViewModelFactory(ProfileLibrary()))
//) {
//    val sortedProfile by viewModel.sortedProfile.collectAsState()
//
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color(0xFFD8D2C2))
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        itemsIndexed(sortedProfile) { _, item ->
//            ProfileItem(
//                name = item.name,
//                photoUrl = item.photoUrl,
//                deskripsi = item.deskripsi
//            )
//        }
//    }
//}

//@Composable
//fun LazyColumnAbs(
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val application = context.applicationContext as Application
//
//    val viewModel: MainViewModel = viewModel(
//        factory = ViewModelFactory(application, ProfileLibrary())
//    )
//
//    val sortedProfile by viewModel.sortedProfile.collectAsState()
//    val localProfiles by viewModel.localProfiles.collectAsState()
//
//    LazyColumn(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color(0xFFD8D2C2))
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        // Menampilkan profil dari repository
//        itemsIndexed(sortedProfile) { _, item ->
//            ProfileItem(
//                name = item.name,
//                photoUrl = item.photoUrl,
//                deskripsi = item.deskripsi
//            )
//        }
//
//        // Menampilkan profil dari local storage
//        items(localProfiles) { profile ->
//            LocalProfileItem(
//                name = profile.name,
//                photoPath = profile.photoPath,
//                description = profile.description
//            )
//        }
//    }
//}

@Composable
fun LazyColumnAbs(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: MainViewModel = viewModel(
        factory = ViewModelFactory(application, ProfileLibrary())
    )

    val profiles by viewModel.profiles.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFD8D2C2))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(profiles) { profile ->
            FirebaseProfileItem(
                profile = profile,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FirebaseProfileItem(
    profile: ProfileData,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
            .clickable { showDialog = true }
    ) {
        AsyncImage(
            model = profile.photoUrl,
            contentDescription = profile.name,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = profile.name,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Text(
                text = profile.description,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }

    if (showDialog) {
        FirebaseProfileDialog(profile = profile, onDismiss = { showDialog = false })
    }
}

@Composable
fun FirebaseProfileDialog(
    profile: ProfileData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = profile.name) },
        text = {
            Column {
                AsyncImage(
                    model = profile.photoUrl,
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = profile.description)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun LocalProfileItem(
    name: String,
    photoPath: String,
    description: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load bitmap from file
    LaunchedEffect(photoPath) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(photoPath)
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(photoPath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
            .clickable { showDialog = true }
    ) {
        bitmap?.let { btm ->
            Image(
                bitmap = btm.asImageBitmap(),
                contentDescription = name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } ?: Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Text(
                text = description,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }

    if (showDialog) {
        LocalProfileDialog(
            name = name,
            bitmap = bitmap,
            description = description,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun LocalProfileDialog(
    name: String,
    bitmap: Bitmap?,
    description: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = name) },
        text = {
            Column {
                bitmap?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = description)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@SuppressLint("RememberReturnType")
@Composable
fun ProfileItem(
    name: String,
    photoUrl: Int,
    deskripsi: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
            .clickable { showDialog = true }
    ) {
        Image(
            painter = painterResource(id = photoUrl),
            contentDescription = name,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Text(
                text = deskripsi,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }

    if (showDialog) {
        ProfileDialog(
            name = name,
            photoUrl = photoUrl,
            deskripsi = deskripsi,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ProfileDialog(
    name: String,
    photoUrl: Int,
    deskripsi: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = name) },
        text = {
            Column {
                Image(
                    painter = painterResource(id = photoUrl),
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = deskripsi)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

