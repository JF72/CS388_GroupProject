package com.example.taro.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/** Database object that lists all necessary entities **/
@Database(entities = [UserTaskDb::class,UserMoodDb::class], version = 1)
abstract  class TaroDb : RoomDatabase(){
    abstract  fun taroDao() : TaroDao
    companion object {

        @Volatile
        private var INSTANCE: TaroDb? = null

        fun getInstance(context: Context): TaroDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaroDb::class.java, "Taro-db"
            ).build()
    }
}