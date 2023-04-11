package com.group11.healthtrackerapp.UserInfo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.DateFormatSymbols
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.ExerciseTracker.MonthlyExercisePage
import com.group11.healthtrackerapp.FoodTracker.MonthlyFoodPage
import com.group11.healthtrackerapp.FoodTracker.repository.FoodNutrition
import com.group11.healthtrackerapp.FoodTracker.repository.FoodNutritionDao
import com.group11.healthtrackerapp.MainActivity
import com.group11.healthtrackerapp.R
import com.group11.healthtrackerapp.analytics.AnalyticsPage
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.Date
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/*
Initial setup page where users go after first installing/resetting the app.
 */
class InitialSetupPage : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var dateOfBirth: Button
    private lateinit var gender: Spinner
    private lateinit var weight: EditText
    private lateinit var height: EditText
    private lateinit var targetWeight: EditText
    private lateinit var signUpButton: Button
    private lateinit var user: User
    private lateinit var bottomNavigationView: BottomNavigationView
    private val MinimumWeightLossPercentage = 0.10
    private lateinit var userDao: UserDao
    private lateinit var foodDao: FoodNutritionDao
    private val MaximumWeightLossPerDay = 0.45

    fun isValidDate(str: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.isLenient = false
        try {
            dateFormat.parse(str.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return false
        }
        return true
    }

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            signUpButton.isEnabled =
                firstName.text.isNotEmpty() && lastName.text.isNotEmpty() && user.dateOfBirth.isNotEmpty()
                        && isValidDate(user.dateOfBirth) && weight.text.isNotEmpty() && height.text.isNotEmpty() && targetWeight.text.isNotEmpty()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "User Information";
        setContentView(R.layout.activity_initial_setup_page)

        userDao = AppDatabase.getDatabase(applicationContext).userDao()
        foodDao=AppDatabase.getDatabase(applicationContext).FoodNutritionDao()

        signUpButton = findViewById(R.id.sign_up)
        firstName = findViewById(R.id.input_first_name)
        lastName = findViewById(R.id.input_last_name)
        dateOfBirth = findViewById(R.id.input_date_of_birth)
        gender = findViewById(R.id.input_gender)
        weight = findViewById(R.id.input_weight)
        height = findViewById(R.id.input_height)
        targetWeight = findViewById(R.id.input_target_weight)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        user = User()
        val currentDate = LocalDateTime.now()

        signUpButton.isEnabled = false
        firstName.addTextChangedListener(textWatcher)
        lastName.addTextChangedListener(textWatcher)
        dateOfBirth.addTextChangedListener(textWatcher)
        weight.addTextChangedListener(textWatcher)
        height.addTextChangedListener(textWatcher)
        targetWeight.addTextChangedListener(textWatcher)

        if (userDao.getLastOne() != null) {
            user = userDao.getLastOne()!!
            firstName.setText(user.firstName)
            lastName.setText(user.lastName)
            val dob = LocalDate.parse(user.dateOfBirth, DateTimeFormatter.ofPattern("yyyy-M-d"))
            dateOfBirth.text = "${dob.month} ${dob.dayOfMonth}, ${dob.year}"
            gender.setSelection(resources.getStringArray(R.array.gender).indexOf(user.gender))
            weight.setText(user.weight.toString())
            height.setText(user.height.toString())
            targetWeight.setText(user.targetWeight.toString())
            signUpButton.text = "Save"

            //Navigation Bar Setup
            bottomNavigationView.visibility = View.VISIBLE
            bottomNavigationView.selectedItemId = R.id.settings
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
                        startActivity(Intent(this, MonthlyExercisePage::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }
                    R.id.settings -> {
                        //startActivity(Intent(this, InitialSetupPage::class.java))
                        //overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }
                    else -> return@setOnItemSelectedListener false
                }
            }
        } else {
            bottomNavigationView.visibility = View.GONE
        }

        signUpButton.setOnClickListener(View.OnClickListener {
            val firstNameSQL: String = firstName.text.toString()
            val lastNameSQL: String = lastName.text.toString()
            val dateOfBirthSQL: Date = Date.valueOf(user.dateOfBirth)
            val genderSQL: String = gender.selectedItem.toString()
            val heightSQL: Int = height.text.toString().toInt()
            val weightSQL: Int = weight.text.toString().toInt()
            val targetWeightSQL: Int = targetWeight.text.toString().toInt()
            val currentDateSQL: Date =
                Date.valueOf(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

            val newUser=User(0,firstNameSQL,lastNameSQL,user.dateOfBirth,genderSQL,heightSQL,weightSQL,targetWeightSQL,currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            checkWeightLoss(targetWeightSQL, weightSQL, newUser)
            writeFoodDummyData()
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var dateStringDisplay =
            DateFormatSymbols().months[month] + " " + dayOfMonth + " , " + year
        var monthForData=month+1
        var dateString = "$year-$monthForData-$dayOfMonth"
        var dateOfBirth = findViewById<Button>(R.id.input_date_of_birth)
        dateOfBirth.text = dateStringDisplay
        user.dateOfBirth = dateString
    }

    fun showDatePickerDialog(v: View) {
        val currentDate = LocalDateTime.now()
        DatePickerDialog(
            this, this, currentDate.year,
            currentDate.monthValue - 1, currentDate.dayOfMonth
        ).show()
    }

    private fun checkWeightLoss(targetWeight: Int, currentWeight: Int, newUser: User){
        if(targetWeight<(1-MinimumWeightLossPercentage)*currentWeight||checkCurrentWeight()){
            var targetWeight = findViewById<EditText>(R.id.input_target_weight)
            targetWeight.text.clear()
            val alertDialogBuilder = AlertDialog.Builder(this)
            val text = "It's important to lose weight in a gradual and healthy manner, rather than trying to lose too much too quickly."

            with(alertDialogBuilder){
                setTitle("Lose Weight Alert")
                setMessage(text)
                setCancelable(false)
                setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
                show()
            }
            val alertDialog = alertDialogBuilder.create()
            signUpButton.isEnabled = false
        }else{
            userDao.insert(newUser)
            val text = "Succeeded!"
            val duration = Toast.LENGTH_LONG
            Toast.makeText(applicationContext, text, duration).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun checkCurrentWeight():Boolean{
        val weightNumber = weight.text.toString().toInt()
        val currentDate: Date =
                Date.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        val firstUser = userDao.getFirstOne()
        val firstDate: Date
        val firstWeight: Int
        if(firstUser !=null){
            firstDate = Date.valueOf(firstUser.currentDate)
            firstWeight = firstUser.weight
            if(firstWeight-weightNumber>dateMinue(firstDate,currentDate)*MaximumWeightLossPerDay){
                return true
            }
        }
        return false
    }

    private fun dateMinue(date1: Date, date2: Date): Double {
        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return days.toDouble()
    }

    private fun writeFoodDummyData(){
        var newFoodEntry = FoodNutrition(0, "apple", 2023, 3, 30, 100.0, "g", 100.0, 4.0, 2.0, 1.76, 0.0, 20.0, 5.0, 15.0, 1.2, 4.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "banana", 2023, 3, 30, 200.0, "g", 200.0, 4.0, 3.0, 1.96, 0.0, 29.0, 5.8, 19.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "pear", 2023, 3, 30, 200.0, "g", 200.0, 4.0, 3.0, 1.96, 0.0, 29.0, 5.8, 19.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "strawberries", 2023, 3, 30, 150.0, "g", 148.0, 4.0, 1.0, 1.16, 0.0, 39.0, 5.9, 29.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "apple", 2023, 3, 31, 100.0, "g", 100.0, 4.0, 2.0, 1.76, 0.0, 20.0, 5.0, 15.0, 1.2, 4.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "banana", 2023, 3, 31, 200.0, "g", 200.0, 4.0, 3.0, 1.96, 0.0, 29.0, 5.8, 19.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "pear", 2023, 3, 31, 200.0, "g", 200.0, 4.0, 3.0, 1.96, 0.0, 29.0, 5.8, 19.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
        newFoodEntry = FoodNutrition(0, "strawberries", 2023, 3, 31, 150.0, "g", 148.0, 4.0, 1.0, 1.16, 0.0, 39.0, 5.9, 29.0, 1.9, 1.3, )
        foodDao.insert(newFoodEntry)
    }
}