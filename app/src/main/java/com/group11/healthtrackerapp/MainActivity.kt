package com.group11.healthtrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.group11.healthtrackerapp.ExerciseTracker.MonthlyExercisePage
import com.group11.healthtrackerapp.ExerciseTracker.repository.ExerciseTrackerUtils
import com.group11.healthtrackerapp.ExerciseTracker.repository.logDummyData
import com.group11.healthtrackerapp.FoodTracker.MonthlyFoodPage
import com.group11.healthtrackerapp.UserInfo.InitialSetupPage
import com.group11.healthtrackerapp.analytics.AnalyticsPage
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import java.io.File

/*
The "main" activity of our Android app. This will serve as the home screen of our app
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stetho.initializeWithDefaults(this)

        OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        val userDao = AppDatabase.getDatabase(this).userDao()

        val logFile = File(this.filesDir, "exercise_tracker.txt")
        logFile.delete()
        logFile.createNewFile()
        val exerciseTrackerUtils = ExerciseTrackerUtils(logFile)

        logDummyData(logFile, exerciseTrackerUtils)

        if (userDao.getLastOne() == null) {
            startActivity(Intent(this, InitialSetupPage::class.java))
        } else {
            startActivity(Intent(this, AnalyticsPage::class.java))
            val initialSetupButton: Button = findViewById<Button>(R.id.initial_setup)
            initialSetupButton.setOnClickListener {
                val intent = Intent(this, InitialSetupPage::class.java)
                startActivity(intent)
            }

            val exerciseTrackerButton: Button = findViewById<Button>(R.id.exercise_tracker)
            exerciseTrackerButton.setOnClickListener {
                val intent = Intent(this, MonthlyExercisePage::class.java)
                startActivity(intent)
            }

            val foodTrackerButton: Button = findViewById<Button>(R.id.food_tracker)
            foodTrackerButton.setOnClickListener {
                val intent = Intent(this, MonthlyFoodPage::class.java)
                startActivity(intent)
            }

            val analyticsButton: Button = findViewById<Button>(R.id.analytics)
            analyticsButton.setOnClickListener {
                val intent = Intent(this, AnalyticsPage::class.java)
                startActivity(intent)
            }

        }
    }
}