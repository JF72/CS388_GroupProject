package com.example.taro

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNav {

    fun setupBottomNav(bottomNavView: BottomNavigationView, context: Context) {
        bottomNavView.setOnItemSelectedListener { item ->
            val currentActivity = context as? Activity

            when (item.itemId) {
                R.id.nav_home -> {
                    if (currentActivity?.javaClass != TaroHomePage::class.java) {
                        context.startActivity(Intent(context, TaroHomePage::class.java))
                        currentActivity?.finish()
                    }
                    true
                }
                R.id.nav_tasks -> {
                    if (currentActivity?.javaClass != TaroTasksPage::class.java) {
                        context.startActivity(Intent(context, TaroTasksPage::class.java))
                        currentActivity?.finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (currentActivity?.javaClass != TaroProfilePage::class.java) {
                        context.startActivity(Intent(context, TaroProfilePage::class.java))
                        currentActivity?.finish()
                    }
                    true
                }
                R.id.nav_statistics -> {
                    if (currentActivity?.javaClass != TaroStatsPage::class.java) {
                        context.startActivity(Intent(context, TaroStatsPage::class.java))
                        currentActivity?.finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
