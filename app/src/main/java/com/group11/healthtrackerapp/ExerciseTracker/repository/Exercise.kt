package com.group11.healthtrackerapp.ExerciseTracker.repository

import java.time.LocalDateTime

class Exercise(
    val dateTime: LocalDateTime,
    val duration: Duration,
    val exerciseType: ExerciseType,
    val caloriesBurned: Int,
    val description: String,

    var isExpanded: Boolean = false
) {
    enum class ExerciseType {
        UPPER, LOWER, CARDIO
    }

    data class Duration(val hours: Int, val minutes: Int)
}