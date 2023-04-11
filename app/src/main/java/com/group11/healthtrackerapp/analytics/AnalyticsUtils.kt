package com.group11.healthtrackerapp.analytics


import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.ExerciseTracker.repository.ExerciseTrackerUtils
import com.group11.healthtrackerapp.R
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class AnalyticsUtils (private val exerciseTrackerUtils: ExerciseTrackerUtils, private val context: Context) {
    // class that prepare chart data

    fun weightsLineChart(): LineData {
        val userDao = AppDatabase.getDatabase(context).userDao()
        val user = userDao.getAll()
        val entries = user.map {
            Entry(
                LocalDate.parse(it.currentDate).toEpochDay().toFloat(),
                it.weight.toFloat()
            )
        }.toMutableList()
        val lineDataSet = LineDataSet(entries, "")
        lineDataSet.apply {
            color = ContextCompat.getColor(context, R.color.purple_500)
            highLightColor = ContextCompat.getColor(context, R.color.dark_grey)
            circleColors = arrayListOf(ContextCompat.getColor(context, R.color.purple_500))
        }
        return LineData(lineDataSet)
    }

    fun caloriesBarChart(): Pair<MutableList<BarEntry>, MutableList<BarEntry>> {
        val exercises = exerciseTrackerUtils.loadExercise()
        val foodNutritionDao = AppDatabase.getDatabase(context).FoodNutritionDao()
        val foods = foodNutritionDao.getAll()

        val foodCaloriesByDate = foods.groupBy { LocalDate.of(it.edtYearValue, it.edtMonthValue, it.edtDayValue) }
            .mapValues { (_, foods) -> foods.sumOf { it.nf_calories } }


        // Create a map of dates to calories burned
        val exerciseCaloriesByDate = exercises.groupBy { it.dateTime.toLocalDate() }
            .mapValues { (_, exercises) -> exercises.sumOf { it.caloriesBurned } }

        // Create a list of all dates with exercises, foods
        val datesWithExercises = exerciseCaloriesByDate.keys.sorted()
        val datesWithFoods = foodCaloriesByDate.keys.sorted()

        val caloriesBurnedEntries = mutableListOf<BarEntry>()
        val caloriesIntakeEntries = mutableListOf<BarEntry>()

        // Iterate over all dates in the desired range and add entries with 0 calories for dates with no exercises/foods
        val startDate: LocalDate;
        val endDate: LocalDate;
        if (datesWithExercises.isEmpty()){
            startDate = datesWithFoods.first()
            endDate = datesWithFoods.last()
        } else if ( datesWithFoods.isEmpty() ){
            startDate = datesWithExercises.first()
            endDate = datesWithExercises.last()
        } else{
            startDate = minOf(datesWithExercises.first(), datesWithFoods.first())
            endDate = maxOf(datesWithExercises.first(), datesWithFoods.first())
        }
        var currentDate = startDate
        while (currentDate <= endDate) {
            val date = currentDate.toEpochDay().toFloat() // Convert date to a float value
            val caloriesBurned = exerciseCaloriesByDate[currentDate] ?: 0
            val caloriesIntake = foodCaloriesByDate[currentDate] ?: 0

            caloriesBurnedEntries.add(BarEntry(date, caloriesBurned.toFloat()))
            caloriesIntakeEntries.add(BarEntry(date, caloriesIntake.toFloat()))
            currentDate = currentDate.plusDays(1)
        }
        return Pair(caloriesBurnedEntries, caloriesIntakeEntries)
    }

    fun nutrientRadarChart(): RadarData{
        val foodNutritionDao = AppDatabase.getDatabase(context).FoodNutritionDao()
        val foods = foodNutritionDao.getAll()

        val foodByDate = foods.groupBy { LocalDate.of(it.edtYearValue, it.edtMonthValue, it.edtDayValue) }[LocalDate.now()] ?: emptyList()

        val proteinActual = foodByDate.sumOf { it.nf_protein }
        val carbsActual = foodByDate.sumOf { it.nf_total_carbohydrate }
        val fatActual = foodByDate.sumOf { it.nf_total_fat }

        val buf = calculateDailyRecommendIntake()
        val proteinRecommended = buf.first
        val carbsRecommended = buf.second
        val fatRecommended = buf.third

        val entries1: MutableList<Float> = ArrayList()
        entries1.add(proteinActual.toFloat())
        entries1.add(carbsActual.toFloat())
        entries1.add(fatActual.toFloat())

        val entries2: MutableList<Float> = ArrayList()
        entries2.add(proteinRecommended.toFloat())
        entries2.add(carbsRecommended.toFloat())
        entries2.add(fatRecommended.toFloat())

        val radarDataSet1: RadarDataSet
        val radarDataSet2: RadarDataSet

        radarDataSet1 = RadarDataSet(entries1.map { RadarEntry(it) }, "Actual Intake")
        radarDataSet1.color = ContextCompat.getColor(context, R.color.purple_500)
        radarDataSet1.fillColor = ContextCompat.getColor(context, R.color.purple_500)
        radarDataSet1.setDrawFilled(true)
        radarDataSet1.fillAlpha = 100
        radarDataSet1.lineWidth = 2f
        radarDataSet1.isDrawHighlightCircleEnabled = true
        radarDataSet1.setDrawHighlightIndicators(false)

        radarDataSet2 = RadarDataSet(entries2.map { RadarEntry(it) }, "Recommended Intake")
        radarDataSet2.color = ContextCompat.getColor(context, R.color.teal_200)
        radarDataSet2.fillColor = ContextCompat.getColor(context, R.color.teal_200)
        radarDataSet2.setDrawFilled(true)
        radarDataSet2.fillAlpha = 100
        radarDataSet2.lineWidth = 2f
        radarDataSet2.isDrawHighlightCircleEnabled = true
        radarDataSet2.setDrawHighlightIndicators(false)

        val dataSets: MutableList<IRadarDataSet> = ArrayList()
        dataSets.add(radarDataSet1)
        dataSets.add(radarDataSet2)

        return RadarData(dataSets)
    }

    @SuppressLint("Range")
    fun calculateDailyRecommendIntake(): Triple<Double, Double, Double> {
        val userDao= AppDatabase.getDatabase(context).userDao()
        val lastUserInfo=userDao.getLastOne();

        if (lastUserInfo!==null) {
            val firstName = lastUserInfo.firstName
            val lastName = lastUserInfo.lastName
            val dateOfBirth = lastUserInfo.dateOfBirth
            val gender = lastUserInfo.gender
            val height = lastUserInfo.height
            val weight = lastUserInfo.weight
            val targetWeight = lastUserInfo.targetWeight

            val age = ChronoUnit.YEARS.between(
                LocalDate.of(
                    dateOfBirth.split('-')[0].toInt(),
                    dateOfBirth.split('-')[1].toInt(),
                    dateOfBirth.split('-')[2].toInt()
                ),
                LocalDate.now()
            )


            val BMR = if (gender == "male") {
                88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age)
            } else {
                447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age)
            }
            val TDEE = BMR * 1.2 // assuming sedentary activity level
            val protein = weight * 0.8
            val carbohydrate = TDEE * 0.45 / 4
            val fat = (TDEE - (protein * 4) - (carbohydrate * 4)) / 9
            return Triple(protein, carbohydrate, fat)
        }
        return Triple(0.0, 0.0, 0.0)
    }

    fun calorieBudgetChart(): BarData {
        val budget = calculateDailyCalorieBudget()
        val calorieIntake = budget.first
        val netCalories = budget.second
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0f, floatArrayOf(calorieIntake, netCalories)))
        val barDataSet = BarDataSet(entries, "")
        val colors = arrayListOf(
            ContextCompat.getColor(context, R.color.teal_200),
            ContextCompat.getColor(context, R.color.light_grey)
        )
        barDataSet.colors = colors
        return BarData(barDataSet)
    }

    fun calculateDailyCalorieBudget(): Pair<Float, Float> {
        val userDao= AppDatabase.getDatabase(context).userDao()
        val lastUserInfo=userDao.getLastOne();

        if (lastUserInfo !== null) {
            val dateOfBirth = lastUserInfo.dateOfBirth
            val gender = lastUserInfo.gender
            val height = lastUserInfo.height
            val targetWeight = lastUserInfo.targetWeight
            val age = ChronoUnit.YEARS.between(
                LocalDate.of(
                    dateOfBirth.split('-')[0].toInt(),
                    dateOfBirth.split('-')[1].toInt(),
                    dateOfBirth.split('-')[2].toInt()
                ),
                LocalDate.now()
            )
            val calorieBudget = if (gender == "male") {
                66.47 + (13.75 * targetWeight) + (5.003 * height) - (6.755 * age)
            } else {
                655.1 + (9.563 * targetWeight) + (1.850 * height) - (4.676 * age)
            }

            val foodNutritionDao = AppDatabase.getDatabase(context).FoodNutritionDao()
            val foods = foodNutritionDao.getAll()

            val foodByDate = foods.groupBy { LocalDate.of(it.edtYearValue, it.edtMonthValue, it.edtDayValue) }[LocalDate.now()] ?: emptyList()
            val calorieIntake = foodByDate.sumOf { it.nf_calories }
            val netCalories = calorieBudget - calorieIntake
            return Pair(calorieIntake.toFloat(), netCalories.toFloat())
        }
        return Pair(0f, 0f)
    }
}

class XAxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong() * 1000 * 60 * 60 * 24)
        return SimpleDateFormat("MMM dd", Locale.ENGLISH).format(date)
    }
}