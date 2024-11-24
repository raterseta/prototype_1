//package com.example.prototype_1
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.prototype_1.Model.ProfileLibrary
//import com.example.prototype_1.Model.ProfilesHead
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//
//class MainViewModel(private val repository: ProfileLibrary): ViewModel() {
//
//    private val _sortedProfile = MutableStateFlow(
//        repository.getProfilesHead()
//
//    )
//
//    val sortedProfile : MutableStateFlow<List<ProfilesHead>> get() = _sortedProfile
//
//
//}
//
//class ViewModelFactory(private val repository: ProfileLibrary) :
//    ViewModelProvider.NewInstanceFactory() {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return MainViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
//    }
//
//}
//
////File CreateActivity.k
//
//
//Bisakah anda memodifikasi tanpa menghapus ini?


//package com.example.prototype_1
//
//import android.app.Application
//import android.graphics.Bitmap
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.prototype_1.Model.LocalProfile
//import com.example.prototype_1.Model.ProfileData
//import com.example.prototype_1.Model.ProfileLibrary
//import com.example.prototype_1.Model.ProfilesHead
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import java.io.ByteArrayOutputStream
//
//class MainViewModel(
//    application: Application,
//    private val repository: ProfileLibrary
//) : AndroidViewModel(application) {
//
//    // Untuk data dari repository
//    private val _sortedProfile = MutableStateFlow(
//        repository.getProfilesHead()
//    )
//    val sortedProfile: MutableStateFlow<List<ProfilesHead>> get() = _sortedProfile
//
//    // Untuk data dari local storage
//    private val _localProfiles = MutableStateFlow<List<LocalProfile>>(emptyList())
//    val localProfiles: StateFlow<List<LocalProfile>> = _localProfiles
//
//    init {
//        loadLocalProfiles()
//    }
//
//    fun loadLocalProfiles() {
//        viewModelScope.launch {
//            val context = getApplication<Application>().applicationContext
//            val sharedPreferences = context.getSharedPreferences("LocalData", android.content.Context.MODE_PRIVATE)
//            val profilesSet = sharedPreferences.getStringSet("profiles", setOf()) ?: setOf()
//
//            val localProfiles = profilesSet.map { profileString ->
//                val (name, description, photoPath) = profileString.split("|")
//                LocalProfile(name, description, photoPath)
//            }
//
//            _localProfiles.value = localProfiles
//        }
//    }
//}
//
//class ViewModelFactory(
//    private val application: Application,
//    private val repository: ProfileLibrary
//) : ViewModelProvider.AndroidViewModelFactory(application) {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return MainViewModel(application, repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
//    }
//}
//
////Saya bingung, bisakah anda menambahkannya ke sini tanpa menghapus kode yang sudah ada? (tulis ulang + tambahannyay)
////di bawah ini kode yang ingin ditambahkan
//
//class MainViewModel(
//    application: Application,
//    private val repository: ProfileLibrary
//) : AndroidViewModel(application) {
//
//    private val database = Firebase.database("https://prototype-2a1a8-default-rtdb.firebaseio.com/")
//    private val storage = Firebase.storage
//
//    private val _profiles = MutableStateFlow<List<ProfileData>>(emptyList())
//    val profiles: StateFlow<List<ProfileData>> = _profiles
//
//    init {
//        loadProfiles()
//    }
//
//    private fun loadProfiles() {
//        viewModelScope.launch {
//            val profilesRef = database.getReference("profiles")
//            profilesRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val profilesList = mutableListOf<ProfileData>()
//                    for (childSnapshot in snapshot.children) {
//                        childSnapshot.getValue(ProfileData::class.java)?.let {
//                            profilesList.add(it)
//                        }
//                    }
//                    _profiles.value = profilesList.sortedByDescending { it.timestamp }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("MainViewModel", "Error loading profiles", error.toException())
//                }
//            })
//        }
//    }
//
//    fun saveProfile(name: String, description: String, bitmap: Bitmap, onComplete: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            try {
//                // Generate unique ID
//                val profileId = database.getReference("profiles").push().key ?: return@launch
//
//                // Convert bitmap to bytes
//                val baos = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                val imageData = baos.toByteArray()
//
//                // Upload image to Firebase Storage
//                val storageRef = storage.reference.child("profile_images/$profileId.jpg")
//                val uploadTask = storageRef.putBytes(imageData)
//
//                uploadTask.continueWithTask { task ->
//                    if (!task.isSuccessful) {
//                        task.exception?.let { throw it }
//                    }
//                    storageRef.downloadUrl
//                }.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val photoUrl = task.result.toString()
//
//                        // Create profile data
//                        val profileData = ProfileData(
//                            id = profileId,
//                            name = name,
//                            description = description,
//                            photoUrl = photoUrl
//                        )
//
//                        // Save to Realtime Database
//                        database.getReference("profiles")
//                            .child(profileId)
//                            .setValue(profileData)
//                            .addOnCompleteListener { dbTask ->
//                                onComplete(dbTask.isSuccessful)
//                            }
//                    } else {
//                        onComplete(false)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("MainViewModel", "Error saving profile", e)
//                onComplete(false)
//            }
//        }
//    }
//}

package com.example.prototype_1

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.prototype_1.Model.LocalProfile
import com.example.prototype_1.Model.ProfileData
import com.example.prototype_1.Model.ProfileLibrary
import com.example.prototype_1.Model.ProfilesHead
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainViewModel(
    application: Application,
    private val repository: ProfileLibrary
) : AndroidViewModel(application) {

    // Firebase references
    private val database = Firebase.database("https://zfpcwcsoqqglstogbtmx.supabase.co")
//
//    private val database = Firebase.database("gs://fir-auth-47d52.firebasestorage.app")

    private val storage = Firebase.storage

    // StateFlow for Firebase profiles
    private val _profiles = MutableStateFlow<List<ProfileData>>(emptyList())
    val profiles: StateFlow<List<ProfileData>> = _profiles

    // StateFlow for sorted profiles from repository
    private val _sortedProfile = MutableStateFlow(
        repository.getProfilesHead()
    )
    val sortedProfile: MutableStateFlow<List<ProfilesHead>> get() = _sortedProfile

    // StateFlow for local profiles
    private val _localProfiles = MutableStateFlow<List<LocalProfile>>(emptyList())
    val localProfiles: StateFlow<List<LocalProfile>> = _localProfiles

    init {
        loadProfiles()
        loadLocalProfiles()
    }

    // Load profiles from Firebase Realtime Database
    private fun loadProfiles() {
        viewModelScope.launch {
            val profilesRef = database.getReference("profiles")
            profilesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profilesList = mutableListOf<ProfileData>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(ProfileData::class.java)?.let {
                            profilesList.add(it)
                        }
                    }
                    _profiles.value = profilesList.sortedByDescending { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainViewModel", "Error loading profiles", error.toException())
                }
            })
        }
    }

    // Save profile to Firebase
    fun saveProfile(name: String, description: String, bitmap: Bitmap, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val profileId = database.getReference("profiles").push().key ?: return@launch
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageData = baos.toByteArray()

                val storageRef = storage.reference.child("profile_images/$profileId.jpg")
                val uploadTask = storageRef.putBytes(imageData)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val photoUrl = task.result.toString()
                        val profileData = ProfileData(
                            id = profileId,
                            name = name,
                            description = description,
                            photoUrl = photoUrl
                        )
                        database.getReference("profiles")
                            .child(profileId)
                            .setValue(profileData)
                            .addOnCompleteListener { dbTask ->
                                onComplete(dbTask.isSuccessful)
                            }
                    } else {
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error saving profile", e)
                onComplete(false)
            }
        }
    }

    // Load profiles from local storage
    fun loadLocalProfiles() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val sharedPreferences = context.getSharedPreferences("LocalData", android.content.Context.MODE_PRIVATE)
            val profilesSet = sharedPreferences.getStringSet("profiles", setOf()) ?: setOf()

            val localProfiles = profilesSet.map { profileString ->
                val (name, description, photoPath) = profileString.split("|")
                LocalProfile(name, description, photoPath)
            }

            _localProfiles.value = localProfiles
        }
    }
}

class ViewModelFactory(
    private val application: Application,
    private val repository: ProfileLibrary
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
