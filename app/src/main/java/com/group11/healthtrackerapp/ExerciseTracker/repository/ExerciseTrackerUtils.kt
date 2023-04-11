package com.group11.healthtrackerapp.ExerciseTracker.repository

import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//fun sampleUsage() {
//    val logFile = File("exercise_log.txt")
//
//    val utils = ExerciseTrackerUtils(logFile)
//
//    // Example usage of calculateCals function
//    val exerciseType = Exercise.ExerciseType.CARDIO
//    val duration = Exercise.Duration(hours = 1, minutes = 30)
//    val caloriesBurned = calculateCals(exerciseType, duration)
//    println("Calories burned: $caloriesBurned")
//
//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//    val dateTimeString = LocalDateTime.now().format(formatter)
//    val dateTime = LocalDateTime.parse(dateTimeString, formatter)
//
//    // Example usage of logExercise function
//    val exercise = Exercise(
//        dateTime = dateTime,
//        duration = duration,
//        exerciseType = exerciseType,
//        caloriesBurned = caloriesBurned.toInt(),
//        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
//    )
//    utils.logExercise(exercise)
//
//    // Example usage of loadExercise function
//    val exercises = utils.loadExercise()
//    for (ex in exercises) {
//        println("${ex.dateTime.format(DateTimeFormatter.ISO_DATE_TIME)} - ${ex.exerciseType}")
//    }
//
//    println()
//
//    // Example usage of findExercisesByDate function
//    val date = LocalDate.now()
//    val exercisesOnDate = utils.findExercisesByDate(date)
//    println("Exercises on $date:")
//    for (ex in exercisesOnDate) {
//        println("${ex.dateTime.format(DateTimeFormatter.ISO_DATE_TIME)} - ${ex.exerciseType}")
//    }
//
//    // Example usage of suggestion function
//    val suggestion = utils.suggestion(date)
//    println("Suggestion for $date: $suggestion")
//}

fun logDummyData(logFile: File, exerciseTrackerUtils: ExerciseTrackerUtils){
    if (logFile.exists()) {
        logFile.delete()
    }
    logFile.createNewFile()

    val exerciseType = Exercise.ExerciseType.CARDIO
    val duration = Exercise.Duration(hours = 1, minutes = 30)
    val caloriesBurned = 82

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTimeString = LocalDateTime.now().format(formatter)
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    val yesterdayTimeString = LocalDateTime.now().minusDays(1).format(formatter)
    val yesterday = LocalDateTime.parse(yesterdayTimeString, formatter)

    val description = "desc"

    val exercise1 = Exercise(
        dateTime = yesterday,
        duration = duration,
        exerciseType = exerciseType,
        caloriesBurned = caloriesBurned.toInt(),
        description = description
    )

    val exercise2 = Exercise(
        dateTime = yesterday,
        duration = duration,
        exerciseType = Exercise.ExerciseType.UPPER,
        caloriesBurned = 120,
        description = description
    )

    val exercise3 = Exercise(
        dateTime = yesterday,
        duration = duration,
        exerciseType = Exercise.ExerciseType.LOWER,
        caloriesBurned = caloriesBurned.toInt(),
        description = description
    )

    val exercise4 = Exercise(
        dateTime = yesterday,
        duration = duration,
        exerciseType = Exercise.ExerciseType.LOWER,
        caloriesBurned = caloriesBurned.toInt(),
        description = description
    )

    val exercise5 = Exercise(
        dateTime = dateTime,
        duration = duration,
        exerciseType = Exercise.ExerciseType.CARDIO,
        caloriesBurned = 93,
        description = description
    )

    val exercise6 = Exercise(
        dateTime = dateTime,
        duration = duration,
        exerciseType = Exercise.ExerciseType.LOWER,
        caloriesBurned = caloriesBurned.toInt(),
        description = description
    )

    val exercise7 = Exercise(
        dateTime = dateTime,
        duration = duration,
        exerciseType = Exercise.ExerciseType.CARDIO,
        caloriesBurned = 380,
        description = description
    )

    val exercise8 = Exercise(
        dateTime = dateTime,
        duration = duration,
        exerciseType = Exercise.ExerciseType.LOWER,
        caloriesBurned = 130,
        description = description
    )

    exerciseTrackerUtils.logExercise(exercise1)
    exerciseTrackerUtils.logExercise(exercise2)
    exerciseTrackerUtils.logExercise(exercise3)
    exerciseTrackerUtils.logExercise(exercise4)
    exerciseTrackerUtils.logExercise(exercise5)
    exerciseTrackerUtils.logExercise(exercise6)
    exerciseTrackerUtils.logExercise(exercise7)
    exerciseTrackerUtils.logExercise(exercise8)


}




//class ExerciseTrackerUtils(val user: User, private val logFile: File) {
class ExerciseTrackerUtils(private val logFile: File) {
    val exercises = loadExercise()

    fun logExercise(exercise: Exercise) {
        println("logged exercise")
        var fileWriter = FileWriter(logFile, true)

        with(exercise) {
            println(dateTime.toString())
            println(dateTime)
            fileWriter.write(dateTime.toString())
            fileWriter.write(System.lineSeparator())
            fileWriter.write("${duration.hours} hours ${duration.minutes} mins")
            fileWriter.write(System.lineSeparator())
            fileWriter.write(exerciseType.toString())
            fileWriter.write(System.lineSeparator())
            fileWriter.write("$caloriesBurned kCals")
            fileWriter.write(System.lineSeparator())
            fileWriter.write(if (description.isEmpty()) "No description." else description)
            fileWriter.write(System.lineSeparator())
            fileWriter.write(System.lineSeparator())
        }
        fileWriter.flush()
        fileWriter.close()
    }

    fun loadExercise(): ArrayList<Exercise> {
        return ArrayList(logFile.readLines().filter { it.isNotBlank() }.chunked(5) { lines ->
            Exercise(
                dateTime = LocalDateTime.parse(lines[0], DateTimeFormatter.ofPattern(("yyyy-MM-dd'T'HH:mm"))),
                duration = Exercise.Duration(
                    hours = lines[1].substringBefore(" hours").toInt(),
                    minutes = lines[1].substringAfter(" hours ").substringBefore(" mins").toInt()
                ),
                exerciseType = Exercise.ExerciseType.valueOf(lines[2]),
                caloriesBurned = lines[3].trimEnd(' ', 'k', 'C', 'a', 'l', 's').toInt(),
                description = lines[4]
            )
        })
    }

    fun findExercisesByDate(date: LocalDate): ArrayList<Exercise> {
        return ArrayList(exercises.filter { it.dateTime.toLocalDate() == date }.toList())

    }


    fun suggestion(date: LocalDate): String {
        // TODO: implement suggestion based on exercises performed on the specified date
        val exercises = findExercisesByDate(date)
        return "No suggestions available"
    }



}



