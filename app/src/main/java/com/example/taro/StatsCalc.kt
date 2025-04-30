package com.example.taro

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

object StatsCalculator {

    fun computeStats(tasks: List<TaskData>): UserStats {
        val dayPointsMap = mutableMapOf<String, Int>()
        val difficultyCompletionMap = mutableMapOf<Int, Pair<Int, Int>>() // difficulty -> (completed, total)
        val weeklyPointsMap = mutableMapOf<Long, Int>()
        val weeklyBreakdownsMap = mutableMapOf<Long, WeekBreakdown>()
        val weeklyDailyBreakdowns = mutableMapOf<Long, MutableMap<String, Int>>()
        var totalTasks = 0
        var completedTasks = 0

        val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
        val weekMillis = 7 * 24 * 60 * 60 * 1000L

        for (task in tasks) {
            totalTasks++
            if (task.completed) completedTasks++

            val day = sdfDay.format(Date(task.createdAt))
            dayPointsMap[day] = dayPointsMap.getOrDefault(day, 0) + task.points

            val (done, total) = difficultyCompletionMap.getOrDefault(task.difficulty, Pair(0, 0))
            val updatedDone = if (task.completed) done + 1 else done
            difficultyCompletionMap[task.difficulty] = Pair(updatedDone, total + 1)

            val week = task.createdAt / weekMillis
            weeklyPointsMap[week] = weeklyPointsMap.getOrDefault(week, 0) + task.points

            // Track full weekly breakdown
            val current = weeklyBreakdownsMap.getOrDefault(week, WeekBreakdown(0, 0, 0))
            weeklyBreakdownsMap[week] = WeekBreakdown(
                totalTasks = current.totalTasks + 1,
                completedTasks = current.completedTasks + if (task.completed) 1 else 0,
                totalPoints = current.totalPoints + task.points
            )

            // Track daily breakdown per week
            val dailyMap = weeklyDailyBreakdowns.getOrPut(week) { mutableMapOf() }
            dailyMap[day] = dailyMap.getOrDefault(day, 0) + task.points
        }

        val mostProductiveDay = dayPointsMap.maxByOrNull { it.value }?.key ?: "None"
        val completionRateByDifficulty = difficultyCompletionMap.mapValues {
            val (done, total) = it.value
            if (total == 0) 0.0 else done.toDouble() / total
        }

        return UserStats(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            mostProductiveDay = mostProductiveDay,
            completionRateByDifficulty = completionRateByDifficulty,
            weeklyPoints = weeklyPointsMap,
            weeklyBreakdowns = weeklyBreakdownsMap,
            weeklyDailyBreakdowns = weeklyDailyBreakdowns
        )
    }
}
