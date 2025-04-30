package com.example.taro

data class TaskData(
    val completed: Boolean,
    val createdAt: Long,
    val points: Int,
    val difficulty: Int
)
