package com.example.taro.Dao;

import androidx.room.ColumnInfo
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime

@Entity(tableName = "user_moods")
data class UserMoodDb(
    @PrimaryKey val moodId : Int,

    @ColumnInfo(name="moodScore") val moodScore : Int,
    @ColumnInfo(name="moodDate") val moodDate : String?,
    @ColumnInfo(name="moodCategory") val moodCategory : String?
)