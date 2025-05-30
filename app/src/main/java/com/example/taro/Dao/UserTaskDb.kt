package com.example.taro.Dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "user_tasks")
data class UserTaskDb(

    @PrimaryKey(autoGenerate = true) val uid : Int = 0,

    @ColumnInfo(name="name") val name : String,
    /** Optional Task description */
    @ColumnInfo(name="description") val description: String? = null,

    @ColumnInfo(name="difficulty") val difficulty : Int,

    @ColumnInfo(name="priority") val priority : Int,

    @ColumnInfo(name="urgency") val urgency : Int,

    @ColumnInfo(name="expectedDuration") val expectedDuration : Double,

    /** Date Object for ease of retrieval */
    @ColumnInfo(name="dueDate") val dueDate : String? = null,

    @ColumnInfo(name="isCompleted") val isCompleted : Boolean,

    @ColumnInfo(name="taroScore") val taroScore : Double? = null,

    /** Time in hours to complete the task **/
    @ColumnInfo(name="completedOn" ) val completedOn : Int? = null

)

