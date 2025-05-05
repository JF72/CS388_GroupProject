package com.example.taro
import kotlinx.serialization.Serializable
import com.bumptech.glide.Priority
import java.time.LocalDateTime

@Serializable
data class UserTask(
    val name: String? = "",
    val description: String? = "",
    val difficulty: Int?,
    val priority: Int?,
    val urgency : Int?,
    val dueDate : String?,
    val isCompleted: Boolean?,
    val taroScore: Double? ,

) : java.io.Serializable