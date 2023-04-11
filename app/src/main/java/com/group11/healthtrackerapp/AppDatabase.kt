package com.group11.healthtrackerapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.group11.healthtrackerapp.FoodTracker.repository.FoodNutrition
import com.group11.healthtrackerapp.UserInfo.User
import com.group11.healthtrackerapp.UserInfo.UserDao
import com.group11.healthtrackerapp.FoodTracker.repository.FoodNutritionDao

@Database(entities = [User::class, FoodNutrition::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun FoodNutritionDao(): FoodNutritionDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase?=null;

        private val MIGRATION: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE if exists users")
                database.execSQL("DROP TABLE if exists user")
                database.execSQL("DROP TABLE if exists foods")
                database.execSQL("CREATE TABLE IF NOT EXISTS 'user' (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        " user_first_name TEXT NOT NULL, " +
                        " user_last_name TEXT NOT NULL, " +
                        " user_dob TEXT NOT NULL, " +
                        " user_gender TEXT NOT NULL, " +
                        " user_height INTEGER NOT NULL, " +
                        " user_weight INTEGER NOT NULL, " +
                        " user_target_weight INT NOT NULL, " +
                        " user_current_date TEXT NOT NULL)")

                val CREATE_FOOD_TABLE = ("CREATE TABLE IF NOT EXISTS foods (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "food_name TEXT NOT NULL," +
                        "edtYearValue INTEGER NOT NULL," +
                        "edtMonthValue INTEGER NOT NULL," +
                        "edtDayValue INTEGER NOT NULL," +
                        "serving_qty REAL NOT NULL," +
                        "serving_unit TEXT NOT NULL," +
                        "nf_calories REAL NOT NULL," +
                        "nf_total_fat REAL NOT NULL," +
                        "nf_saturated_fat REAL NOT NULL," +
                        "nf_cholesterol REAL NOT NULL," +
                        "nf_sodium REAL NOT NULL," +
                        "nf_total_carbohydrate REAL NOT NULL," +
                        "nf_dietary_fiber REAL NOT NULL," +
                        "nf_sugars REAL NOT NULL," +
                        "nf_protein REAL NOT NULL," +
                        "nf_potassium REAL NOT NULL)")

                database.execSQL(CREATE_FOOD_TABLE)
            }
        }

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE;
            if(tempInstance!=null){
                return tempInstance;
            }
            synchronized(this){
                val instance=Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthtracker"
                ).addMigrations(MIGRATION).allowMainThreadQueries().build()
                INSTANCE=instance
                return instance
            }
        }
    }
}