package com.example.prototype_1.pages

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.prototype_1.MainViewModel
import com.example.prototype_1.Model.ProfileLibrary
import com.example.prototype_1.ViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateActivity(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: MainViewModel = viewModel(
        factory = ViewModelFactory(
            application = application,
            repository = ProfileLibrary()
        )
    )

    val coroutineScope = rememberCoroutineScope()

    // State untuk menyimpan input
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }


    // Launcher CameraX
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        try {
            photoBitmap = bitmap
        } catch (e: Exception) {
            // Handle error, misalnya tampilkan pesan error
            e.printStackTrace()
        }
    }
    // Modifikasi fungsi save dengan error handling
    fun saveData() {
        if (name.isNotEmpty() && description.isNotEmpty() && photoBitmap != null) {
            coroutineScope.launch {
                try {
                    saveDataLocally(context, name, description, photoBitmap!!)
                    navController.navigate("home")
                } catch (e: Exception) {
                    // Handle error saving data
                    e.printStackTrace()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Nama
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(50.dp),
            decorationBox = { innerTextField ->
                if (name.isEmpty()) {
                    Text("Enter name", color = Color.Gray, textAlign = TextAlign.Start)
                }
                innerTextField()
            }
        )

        // Input Deskripsi
        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(100.dp),
            decorationBox = { innerTextField ->
                if (description.isEmpty()) {
                    Text("Enter description", color = Color.Gray, textAlign = TextAlign.Start)
                }
                innerTextField()
            }
        )

        // Tampilkan Preview Gambar
        photoBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Photo",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } ?: Text("No photo selected", textAlign = TextAlign.Center)

        // Tombol untuk Membuka Kamera
        FloatingActionButton(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier.size(56.dp)
        ) {
            Text("+", fontSize = 24.sp)
        }

        // Tombol Simpan Data
        Button(
            onClick = {
                if (name.isNotEmpty() && description.isNotEmpty() && photoBitmap != null) {
//                    coroutineScope.launch {
//                        try {
//                            saveDataLocally(context, name, description, photoBitmap!!)
//                            viewModel.loadLocalProfiles() // Reload data after saving
//                            navController.navigate("home")
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            // Handle error here
//                        }
//                    }
                    isLoading = true
                    viewModel.saveProfile(name, description, photoBitmap!!) { success ->
                        isLoading = false
                        if (success) {
                            navController.navigate("home")
                        } else {
                            // Show error message
                            Toast.makeText(
                                context,
                                "Failed to save profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

// Fungsi untuk Menyimpan Data ke Penyimpanan Lokal
//private fun saveDataLocally(context: Context, name: String, description: String, bitmap: Bitmap) {
//    // Simpan gambar ke file lokal
//    val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
//    FileOutputStream(photoFile).use { outputStream ->
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//    }
//
//    // Simpan data ke SharedPreferences
//    val sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//
//    val dataEntry = "$name|$description|${photoFile.absolutePath}"
//    val existingData = sharedPreferences.getStringSet("profiles", mutableSetOf()) ?: mutableSetOf()
//    existingData.add(dataEntry)
//
//    editor.putStringSet("profiles", existingData)
//    editor.apply()
//}
    private fun saveDataLocally(context: Context, name: String, description: String, bitmap: Bitmap) {
        try {
            // Buat direktori jika belum ada
            val cacheDir = context.cacheDir
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val photoFile = File(cacheDir, "${System.currentTimeMillis()}.jpg")

            FileOutputStream(photoFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            val sharedPreferences = context.getSharedPreferences("LocalData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val dataEntry = "$name|$description|${photoFile.absolutePath}"
            val existingData = sharedPreferences.getStringSet("profiles", mutableSetOf()) ?: mutableSetOf()

            // Buat salinan baru dari Set karena SharedPreferences mengembalikan Set yang tidak dapat dimodifikasi
            val newData = HashSet(existingData)
            newData.add(dataEntry)

            editor.putStringSet("profiles", newData)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Re-throw exception untuk ditangani di level atas
        }
    }
