package com.group11.healthtrackerapp.ExerciseTracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.ExerciseTracker.adapter.ExerciseDetailsRecViewAdapter
import com.group11.healthtrackerapp.ExerciseTracker.repository.Exercise
import com.group11.healthtrackerapp.ExerciseTracker.repository.ExerciseTrackerUtils
import com.group11.healthtrackerapp.ExerciseTracker.viewmodels.ExerciseTrackerViewModel
import com.group11.healthtrackerapp.R
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
Activity (page) where users can track and view their exercise progress.
 */
class DailyExercisePage() : AppCompatActivity(){
    var logFile: File? = null
    var exerciseTrackerUtils: ExerciseTrackerUtils? = null
    var date: LocalDate? = null
    var viewmodel: ExerciseTrackerViewModel? = null;

    constructor(date: LocalDate) : this() {
        this.date = date
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.daily_exercise_page)

        logFile = File(this.filesDir, "exercise_tracker.txt")
        exerciseTrackerUtils = ExerciseTrackerUtils(logFile!!)

        viewmodel = ExerciseTrackerViewModel()

        date = LocalDate.parse(intent.getStringExtra("date"))
        val exercises = exerciseTrackerUtils!!.findExercisesByDate(date!!)
        viewmodel!!.exercises = exercises

        val exerciseDetailsRecView = findViewById<RecyclerView>(R.id.ExerciseDetailsRecView)


        // back button
        val backButton = findViewById<Button>(R.id.textTitle)
        backButton.setOnClickListener {
            val intent = Intent(this, MonthlyExercisePage::class.java)
            startActivity(intent)
        }

        // overview
        val totalDurationTxt = findViewById<TextView>(R.id.totalDurationTxt)
        val totalCalsTxt = findViewById<TextView>(R.id.totalCalsTxt)

        totalDurationTxt.text = "Total Duration\n${viewmodel!!.totalDuration(exercises)}"
        totalCalsTxt.text = "Total Calories\n${viewmodel!!.totalCalories(exercises)} kCals"

        // add button
        val btnAdd = findViewById<ImageView>(R.id.btnAdd)
        popupWindow(btnAdd)

        // exercise detail

        val exerciseDetailsRecViewAdapter = ExerciseDetailsRecViewAdapter(this)
        exerciseDetailsRecViewAdapter.setExercises(exercises)

        exerciseDetailsRecView.adapter = exerciseDetailsRecViewAdapter;
        exerciseDetailsRecView.layoutManager = LinearLayoutManager(this)
    }

    private fun getSelectedRadioButtonText(radioGroup: RadioGroup): String {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = radioGroup.findViewById<RadioButton>(checkedRadioButtonId)
        return checkedRadioButton.text.toString()
    }

    fun calculateCals(durationHours : EditText, durationMinutes : EditText, burnedCalories : EditText, exerciseTypeRadioGroup : RadioGroup){
        if (durationHours.text.toString().isNotEmpty() &&
            durationMinutes.text.toString().isNotEmpty() &&
            getSelectedRadioButtonText(exerciseTypeRadioGroup).isNotEmpty()
        ){
            burnedCalories.setText(viewmodel!!.calculateCals(
                Exercise.ExerciseType.valueOf(getSelectedRadioButtonText(exerciseTypeRadioGroup)),
                Exercise.Duration(durationHours.text.toString().toInt(), durationMinutes.text.toString().toInt()),
                this).toInt().toString())

        }
    }

    fun popupWindow(btnAdd: ImageView){

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.details_popup,null)

        val exerciseTypeRadioGroup = dialogLayout.findViewById<RadioGroup>(R.id.exercise_type_radio_group)
        val startTimePicker = dialogLayout.findViewById<TimePicker>(R.id.start_time_picker)
        val durationHours = dialogLayout.findViewById<EditText>(R.id.duration_edit_text)
        val durationMinutes = dialogLayout.findViewById<EditText>(R.id.duration_minutes_edit_text)
        val burnedCalories = dialogLayout.findViewById<EditText>(R.id.burned_calories_edit_text)
        val description = dialogLayout.findViewById<EditText>(R.id.multi_line_edit_text)

        durationHours.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calculateCals(durationHours, durationMinutes, burnedCalories, exerciseTypeRadioGroup)
            }
        })

        durationMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calculateCals(durationHours, durationMinutes, burnedCalories, exerciseTypeRadioGroup)
            }
        })

        exerciseTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            calculateCals(durationHours, durationMinutes, burnedCalories, exerciseTypeRadioGroup)
        }


        btnAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            with(builder){
                setPositiveButton("SUBMIT"){dialog, which ->

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    val dateTimeString = LocalDateTime.of(date!!.year.toString().toInt(), date!!.monthValue.toString().toInt(), date!!.dayOfMonth.toString().toInt(), startTimePicker.hour, startTimePicker.minute, 0).format(formatter)
                    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

                    val exercise = Exercise(
                        dateTime,
                        Exercise.Duration(durationHours.text.toString().toInt(), durationMinutes.text.toString().toInt()),
                        Exercise.ExerciseType.valueOf(getSelectedRadioButtonText(exerciseTypeRadioGroup)),
                        burnedCalories.text.toString().toInt(),
                        description.text.toString()
                    )
                    exerciseTrackerUtils!!.logExercise(exercise)
                    dialog.dismiss()
                    this@DailyExercisePage.recreate()


                }
                setNegativeButton("CANCEL"){dialog, which ->
                    dialog.dismiss()
                    this@DailyExercisePage.recreate()
                }
                setView(dialogLayout)
                show()
            }

        }

    }


}