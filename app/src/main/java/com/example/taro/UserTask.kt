package com.example.taro

import com.bumptech.glide.Priority
import java.time.LocalDateTime


data class UserTask(
    val name: String? = "",
    val description: String? = "",
    val difficulty: Int?,
    val priority: Int?,
    val urgency : Int?,
    val dueDate : LocalDateTime?,
    val isCompleted: Boolean?,
    val taroScore: Double? ,

) : java.io.Serializable