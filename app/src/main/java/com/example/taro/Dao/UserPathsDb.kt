package com.example.taro.Dao

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_tasks")
data class UserPathsDb(

    @PrimaryKey(autoGenerate = true) val uid : Int = 0,

    @ColumnInfo(name="dueDate") val dueDate : String? = null,


    @ColumnInfo(name="Path") val Path : List<String> = null


){

}

