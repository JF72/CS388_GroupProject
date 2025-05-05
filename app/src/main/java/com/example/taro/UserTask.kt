package com.example.taro




data class UserTask(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val points: Int = 0,
    val completed: Boolean = false
)