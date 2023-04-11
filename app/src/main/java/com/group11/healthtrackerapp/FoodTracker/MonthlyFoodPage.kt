package com.group11.healthtrackerapp.FoodTracker

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.ExerciseTracker.MonthlyExercisePage
import com.group11.healthtrackerapp.R
import com.group11.healthtrackerapp.UserInfo.InitialSetupPage
import com.group11.healthtrackerapp.analytics.AnalyticsPage
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DecimalFormat
import java.time.LocalDate

class MonthlyFoodPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.monthly_food_page)

        val foodNutritionDao = AppDatabase.getDatabase(this).FoodNutritionDao()
        val foods = foodNutritionDao.getAll()
                .filter { it.edtYearValue == LocalDate.now().year && it.edtMonthValue == LocalDate.now().monthValue }

        val fat = findViewById<TextView>(R.id.fat)
        val carbohydrate = findViewById<TextView>(R.id.carbohydrate)
        val protein = findViewById<TextView>(R.id.protein)
        val calories = findViewById<TextView>(R.id.calories)


        val daysInMonth = LocalDate.now().lengthOfMonth()
        val averageFat = foods.sumOf { it.nf_total_fat } / daysInMonth.toDouble()
        val averageCarbohydrates = foods.sumOf { it.nf_total_carbohydrate } / daysInMonth.toDouble()
        val averageProtein = foods.sumOf { it.nf_protein } / daysInMonth.toDouble()
        val averageCalories = foods.sumOf { it.nf_calories } / daysInMonth.toDouble()

        val df = DecimalFormat("#.##")
        fat.text = "fat\n${df.format(averageFat)} g"
        carbohydrate.text = "carbonhydrate\n${df.format(averageCarbohydrates)} g"
        protein.text = "protein\n${df.format(averageProtein)} g"
        calories.text = "calories\n${df.format(averageCalories)} g"


        //Navigation Bar Setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.foodLog
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.analytics -> {
                    startActivity(Intent(this, AnalyticsPage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.foodLog -> {
                    //startActivity(Intent(this, MonthlyFoodPage::class.java))
                    //overridePendingTransition(0, 0)
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

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val displayedMonth = month + 1;
            Toast.makeText(this, "You selected: $year-$displayedMonth-$dayOfMonth", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DailyFoodPage::class.java)
            intent.putExtra("date", LocalDate.of(year, displayedMonth, dayOfMonth).toString())
            startActivity(intent)
        }
    }
}