package com.example.taro

import android.app.Application
import com.example.taro.Dao.TaroDb

class TaroApplication : Application() {
    val db by lazy { TaroDb.getInstance(this) }
}