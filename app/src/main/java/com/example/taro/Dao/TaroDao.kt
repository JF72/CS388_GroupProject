package com.example.taro.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDateTime

@Dao
interface TaroDao {
    @Query("SELECT * FROM user_tasks")
    fun getAllTasks() : List<UserTaskDb>

    @Query("SELECT * FROM user_tasks WHERE dueDate LIKE :currDate")
    fun fetchTasksByDate(currDate: String) : List<UserTaskDb>

    @Query("UPDATE user_tasks SET isCompleted = :newStatus , completedOn = :completionTime WHERE uid = :taskId ")
    fun updateTaskStatus(newStatus: Boolean,completionTime:Int, taskId : Int)

    /** Generating and creating a taro score **/
    @Query("UPDATE user_tasks SET taroScore = :calculatedTaroScore WHERE  uid = :taskId")
    fun setTaroScore(calculatedTaroScore : Double, taskId : Int)

    @Insert
    fun insertAll(vararg tasks : UserTaskDb)

    /** Refering to moods Table **/
    @Query("SELECT * FROM user_moods")
    fun getAllMoods() : List<UserMoodDb>

}
