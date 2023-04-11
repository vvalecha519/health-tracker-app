package com.group11.healthtrackerapp.analytics

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.group11.healthtrackerapp.ExerciseTracker.MonthlyExercisePage
import com.group11.healthtrackerapp.ExerciseTracker.repository.ExerciseTrackerUtils
import com.group11.healthtrackerapp.FoodTracker.MonthlyFoodPage
import com.group11.healthtrackerapp.R
import com.group11.healthtrackerapp.UserInfo.InitialSetupPage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import kotlin.math.roundToInt


class AnalyticsPage : AppCompatActivity() {
    private lateinit var logFile: File
    private lateinit var calorieBudgetChart: HorizontalBarChart
    private lateinit var caloriesBarChart: BarChart
    private lateinit var nutrientRadarChart: RadarChart
    private lateinit var weightLineChart: LineChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.analytics_page)

        //Navigation Bar Setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.analytics
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.analytics -> {
                    //startActivity(Intent(this, AnalyticsPage::class.java))
                    //overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.foodLog -> {
                    startActivity(Intent(this, MonthlyFoodPage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.exercisePage -> {
                    startActivity(Intent(this, MonthlyExercisePage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(this, InitialSetupPage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        val logFile = File(this.filesDir, "exercise_tracker.txt")
        val exerciseTrackerUtils = ExerciseTrackerUtils(logFile)
        val analyticsUtils = AnalyticsUtils(exerciseTrackerUtils, this)

        val budget = analyticsUtils.calculateDailyCalorieBudget()
        val calorieIntake = findViewById<TextView>(R.id.calorie_intake)
        calorieIntake.text = budget.first.roundToInt().toString()
        val netCalories = findViewById<TextView>(R.id.net_calories)
        netCalories.text = budget.second.roundToInt().toString()

        caloriesBarChart = findViewById(R.id.calories_bar_chart)
        val entries = analyticsUtils.caloriesBarChart()
        val caloriesBurned = BarDataSet(entries.first, "Calories Burned")
        val caloriesIntake = BarDataSet(entries.second, "Calories Consumed")
        caloriesBurned.color = ContextCompat.getColor(this, R.color.purple_500)
        caloriesIntake.color = ContextCompat.getColor(this, R.color.teal_200)
        caloriesBarChart.apply {
            data = BarData(caloriesBurned, caloriesIntake)
            legend.apply {
                textSize = 14f
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                orientation = Legend.LegendOrientation.HORIZONTAL
                xEntrySpace = 20f
            }
            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.apply {
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                enableGridDashedLine(5f, 5f, 0f)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = XAxisValueFormatter()
                enableGridDashedLine(5f, 5f, 0f)
            }
            barData.barWidth = 0.3f
            groupBars(data.xMin - 0.5f, 0.25f, 0.075f)
            setVisibleXRangeMaximum(7f)
            moveViewToX(data.xMax - 6)
            setScaleEnabled(false)
        }

        nutrientRadarChart = findViewById(R.id.nutrient_radar_chart)
        nutrientRadarChart.apply {
            data = analyticsUtils.nutrientRadarChart()
            legend.apply {
                isWordWrapEnabled = true
                textSize = 11f
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            }
            description.isEnabled = false
            isRotationEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(arrayOf("protein", "carbs", "fat"))
                textSize = 8f
            }
            yAxis.isEnabled = false
            extraBottomOffset = -70f
        }

        calorieBudgetChart = findViewById(R.id.calorie_budget_chart)
        calorieBudgetChart.apply {
            data = analyticsUtils.calorieBudgetChart()
            data.setDrawValues(false)
            legend.isEnabled = false
            description.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = data.yMax
            }
            axisRight.isEnabled = false
            xAxis.apply {
                isEnabled = false
                axisMinimum = 0f
                axisMaximum = 0f
            }
            setTouchEnabled(false)
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }

        weightLineChart = findViewById(R.id.weight_line_chart)
        weightLineChart.apply {
            data = analyticsUtils.weightsLineChart()
            legend.isEnabled = false
            description.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.apply {
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
                enableGridDashedLine(5f, 5f, 0f)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = XAxisValueFormatter()
                enableGridDashedLine(5f, 5f, 0f)
                setAvoidFirstLastClipping(true)
            }
            setVisibleXRangeMaximum(3f)
            moveViewToX(data.xMax - 3)
            setScaleEnabled(false)
        }
    }
}