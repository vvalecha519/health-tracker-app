package com.group11.healthtrackerapp.ExerciseTracker.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.ExerciseTracker.repository.Exercise
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExerciseTrackerViewModel {
    var exercises: ArrayList<Exercise>? = null;

    fun totalDurationByMonth(month: LocalDate): String {
        val filteredExercises = exercises!!.filter {
            it.dateTime.month == month.month && it.dateTime.year == month.year
        }
        val totalDuration = filteredExercises.sumOf { it.duration.hours * 60 + it.duration.minutes }
        val hours = totalDuration / 60
        val minutes = totalDuration % 60
        return "${hours}h ${minutes}m"
    }

    fun totalCaloriesByMonth(month: LocalDate): String {
        val filteredExercises = exercises!!.filter {
            it.dateTime.month == month.month && it.dateTime.year == month.year
        }
        val totalCalories = filteredExercises.sumOf { it.caloriesBurned }
        return "$totalCalories"
    }

    fun totalDuration(exercises: ArrayList<Exercise>): String {
        // total exercises duration in parameters
        var mins = 0

        // Iterate through all exercises and add up the durations
        for (exercise in exercises) {
            val duration = exercise.duration
            mins += duration.hours*60 + duration.minutes

        }

        // Convert the total duration to a formatted string and return it
        return "${mins/60}h ${mins%60}m"
    }

    fun totalCalories(exercises: ArrayList<Exercise>): String {
        // total calories in parameters
        var cals = 0

        for (exercise in exercises) {
            cals += exercise.caloriesBurned
        }

        return cals.toString()
    }

    @SuppressLint("Range")
    fun calculateCals(exerciseType: Exercise.ExerciseType, duration: Exercise.Duration, context: Context): Float {
        // calculate calories base on user detail, exercise detail

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


            val ageInYears = ChronoUnit.YEARS.between(
                LocalDate.of(
                    dateOfBirth.split('-')[0].toInt(),
                    dateOfBirth.split('-')[1].toInt(),
                    dateOfBirth.split('-')[2].toInt()
                ),
                LocalDate.now()
            )
            val genderFactor = if (gender == "Male") 1.0f else 0.9f

            val metValue = when (exerciseType) {
                Exercise.ExerciseType.UPPER -> 4.0f
                Exercise.ExerciseType.LOWER -> 6.0f
                Exercise.ExerciseType.CARDIO -> 8.0f
            }
            val totalDurationInHours = duration.hours + duration.minutes / 60.0f

            return (metValue * weight.toFloat() * genderFactor * totalDurationInHours) / ageInYears * 5.0f

        }
        return 0f
    }


}