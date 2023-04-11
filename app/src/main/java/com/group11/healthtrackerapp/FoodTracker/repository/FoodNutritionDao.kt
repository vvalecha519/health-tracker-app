package com.group11.healthtrackerapp.FoodTracker.repository

import androidx.room.*

@Dao
interface FoodNutritionDao {
    @Query("select * from foods")
    fun getAll(): List<FoodNutrition>

    @Insert
    fun insert(food: FoodNutrition)

    @Query("select * from foods order by _id DESC LIMIT 1")
    fun getLastOne(): FoodNutrition?

}