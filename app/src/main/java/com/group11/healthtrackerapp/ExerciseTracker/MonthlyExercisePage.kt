package com.group11.healthtrackerapp.ExerciseTracker


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.group11.healthtrackerapp.ExerciseTracker.repository.ExerciseTrackerUtils
import com.group11.healthtrackerapp.ExerciseTracker.viewmodels.ExerciseTrackerViewModel
import com.group11.healthtrackerapp.FoodTracker.MonthlyFoodPage
import com.group11.healthtrackerapp.R
import com.group11.healthtrackerapp.UserInfo.InitialSetupPage
import com.group11.healthtrackerapp.analytics.AnalyticsPage
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.time.LocalDate

class MonthlyExercisePage: AppCompatActivity() {
    var viewmodel : ExerciseTrackerViewModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.monthly_exercise_page)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val logFile = File(this.filesDir, "exercise_tracker.txt")
        val exerciseTrackerUtils = ExerciseTrackerUtils(logFile)
        viewmodel = ExerciseTrackerViewModel()
        viewmodel!!.exercises = exerciseTrackerUtils.exercises

        //Navigation Bar Setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.exercisePage
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.analytics -> {
                    startActivity(Intent(this, AnalyticsPage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.foodLog -> {
                    startActivity(Intent(this, MonthlyFoodPage::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.exercisePage -> {
                    //startActivity(Intent(this, MonthlyExercisePage::class.java))
                    //overridePendingTransition(0, 0)
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

        // overview
        val totalDurationTxt = findViewById<TextView>(R.id.totalDurationTxt)
        val totalCalsTxt = findViewById<TextView>(R.id.totalCalsTxt)

        totalDurationTxt.text = "Total Duration\n${viewmodel!!.totalDurationByMonth(LocalDate.now())}"
        totalCalsTxt.text = "Total Calories\n${viewmodel!!.totalCaloriesByMonth(LocalDate.now())} kCals"


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val displayedMonth = month + 1;
            Toast.makeText(this, "You selected: $year-$displayedMonth-$dayOfMonth", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DailyExercisePage::class.java)
            intent.putExtra("date", LocalDate.of(year, displayedMonth, dayOfMonth).toString())
            startActivity(intent)
        }




    }


}