package com.example.taro

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaroStatsPage : ComponentActivity(), OnChartValueSelectedListener {

    private lateinit var statsTextView: TextView
    private lateinit var barChart: BarChart
    private lateinit var backButton: Button
    private var stats: UserStats? = null
    private var isShowingDaily = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_stats_page)

        statsTextView = findViewById(R.id.statsTextView)
        barChart = findViewById(R.id.weeklyBarChart)
        backButton = findViewById(R.id.backToWeeklyButton)
        backButton.setOnClickListener {
            stats?.let { displayWeeklyStats(it) }
            backButton.visibility = Button.GONE
        }

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNav.setupBottomNav(bottomNavView, this)
        bottomNavView.selectedItemId = R.id.nav_statistics

        loadAndAnalyzeTasks()
    }

    private fun loadAndAnalyzeTasks() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("tasks")
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.mapNotNull { doc ->
                    TaskData(
                        completed = doc.getBoolean("completed") ?: false,
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        points = (doc.getLong("points") ?: 0L).toInt(),
                        difficulty = (doc.getLong("difficulty") ?: 1L).toInt()
                    )
                }

                stats = StatsCalculator.computeStats(tasks)
                stats?.let { displayWeeklyStats(it) }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load tasks.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayWeeklyStats(stats: UserStats) {
        statsTextView.text = "Click on a bar to see weekly breakdown"
        isShowingDaily = false
        backButton.visibility = Button.GONE

        val sortedWeeks = stats.weeklyPoints.entries.sortedBy { it.key }
        val entries = sortedWeeks.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Points per Week").apply {
            valueTextSize = 12f
            color = getColor(R.color.purple_700)
        }

        barChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            setFitBars(true)
            animateY(800)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "W${value.toInt() + 1}"
                    }
                }
            }

            axisRight.isEnabled = false
            invalidate()

            setOnChartValueSelectedListener(this@TaroStatsPage)
        }
    }

    private fun displayDailyBreakdown(dailyMap: Map<String, Int>, weekNumber: Int) {
        isShowingDaily = true
        backButton.visibility = Button.VISIBLE
        statsTextView.text = "Week $weekNumber Daily Breakdown"

        val dayOrder = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val entries = dayOrder.mapIndexedNotNull { index, day ->
            dailyMap[day]?.let { BarEntry(index.toFloat(), it.toFloat()) }
        }

        val dataSet = BarDataSet(entries, "Points per Day").apply {
            valueTextSize = 12f
            color = getColor(R.color.teal_700)
        }

        barChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            setFitBars(true)
            animateY(800)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return dayOrder.getOrNull(value.toInt()) ?: ""
                    }
                }
            }

            axisRight.isEnabled = false
            invalidate()
            setOnChartValueSelectedListener(null)
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (isShowingDaily) return
        val weekIndex = e?.x?.toInt() ?: return
        val weekKey = stats?.weeklyPoints?.keys?.sorted()?.getOrNull(weekIndex) ?: return
        val dailyMap = stats?.weeklyDailyBreakdowns?.get(weekKey) ?: return
        displayDailyBreakdown(dailyMap, weekIndex + 1)
    }

    override fun onNothingSelected() {
        statsTextView.text = "Click on a bar to see weekly breakdown"
    }
}
