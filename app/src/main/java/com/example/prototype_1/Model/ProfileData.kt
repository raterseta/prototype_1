package com.example.prototype_1.Model

data class ProfileData(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)