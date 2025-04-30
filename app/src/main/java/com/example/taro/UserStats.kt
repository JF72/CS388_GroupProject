package com.example.taro

data class WeekBreakdown(
    val totalTasks: Int,
    val completedTasks: Int,
    val totalPoints: Int
)

data class UserStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val mostProductiveDay: String,
    val completionRateByDifficulty: Map<Int, Double>,
    val weeklyPoints: Map<Long, Int>,
    val weeklyBreakdowns: Map<Long, WeekBreakdown>,
    val weeklyDailyBreakdowns: Map<Long, Map<String, Int>>
)
