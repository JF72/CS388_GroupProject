package com.example.taro

data class User(
    val userId: String? = null,
    val username: String? = null,
    val email: String? = null,
    //val profilePicture: String? = null
    val createdAt: com.google.firebase.Timestamp? = null,
    val totalPoints: Int = 0
)