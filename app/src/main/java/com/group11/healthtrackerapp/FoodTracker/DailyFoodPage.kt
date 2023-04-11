package com.group11.healthtrackerapp.FoodTracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.R
import com.group11.healthtrackerapp.FoodTracker.adapters.FoodDetailsRecViewAdapter
import java.text.DecimalFormat
import java.time.LocalDate

class DailyFoodPage : AppCompatActivity() {

    var date: LocalDate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.daily_food_page)

        date = LocalDate.parse(intent.getStringExtra("date"))

        val foodDetailsRecView = findViewById<RecyclerView>(R.id.FoodDetailsRecView)

        val foodNutritionDao = AppDatabase.getDatabase(this).FoodNutritionDao()
        val foods = foodNutritionDao.getAll().groupBy { LocalDate.of(it.edtYearValue, it.edtMonthValue, it.edtDayValue) }[date!!] ?: emptyList()


        val fat = findViewById<TextView>(R.id.fat)
        val carbohydrate = findViewById<TextView>(R.id.carbohydrate)
        val protein = findViewById<TextView>(R.id.protein)
        val calories = findViewById<TextView>(R.id.totalCalsTxt)

        val df = DecimalFormat("#.##")
        fat.text = "fat\n${df.format(foods.sumOf { it.nf_total_fat })} g"
        carbohydrate.text = "carbohydrate\n${df.format(foods.sumOf { it.nf_total_carbohydrate })} g"
        protein.text = "protein\n${df.format(foods.sumOf { it.nf_protein })} g"
        calories.text = "calories\n${df.format(foods.sumOf { it.nf_calories })} kCals"

        // back button
        val backButton = findViewById<Button>(R.id.textTitle)
        backButton.setOnClickListener {
            val intent = Intent(this, MonthlyFoodPage::class.java)
            startActivity(intent)
        }


        val btnAdd = findViewById<ImageView>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, FoodLoggingActivity::class.java)
            intent.putExtra("date", date.toString())
            startActivity(intent)

        }

        val foodDetailsRecViewAdapter = FoodDetailsRecViewAdapter(this)
        foodDetailsRecViewAdapter.setFood(ArrayList(foods))

        foodDetailsRecView.adapter = foodDetailsRecViewAdapter;
        foodDetailsRecView.layoutManager = LinearLayoutManager(this)
    }

}