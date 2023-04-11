package com.group11.healthtrackerapp.FoodTracker.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodNutrition(
    @PrimaryKey(autoGenerate = true)
    var _id: Int,
    @ColumnInfo(name="food_name")
    var food_name: String,
    @ColumnInfo(name="edtYearValue")
    var edtYearValue: Int,
    @ColumnInfo(name="edtMonthValue")
    var edtMonthValue: Int,
    @ColumnInfo(name="edtDayValue")
    var edtDayValue: Int,
    @ColumnInfo(name="serving_qty")
    var serving_qty: Double,
    @ColumnInfo(name = "serving_unit")
    var serving_unit: String,
    @ColumnInfo(name = "nf_calories")
    var nf_calories: Double,
    @ColumnInfo(name = "nf_total_fat")
    var nf_total_fat: Double,
    @ColumnInfo(name = "nf_saturated_fat")
    var nf_saturated_fat: Double,
    @ColumnInfo(name = "nf_cholesterol")
    var nf_cholesterol: Double,
    @ColumnInfo(name = "nf_sodium")
    var nf_sodium: Double,
    @ColumnInfo(name="nf_total_carbohydrate")
    var nf_total_carbohydrate: Double,
    @ColumnInfo(name="nf_dietary_fiber")
    var nf_dietary_fiber: Double,
    @ColumnInfo(name="nf_sugars")
    var nf_sugars: Double,
    @ColumnInfo(name="nf_protein")
    var nf_protein: Double,
    @ColumnInfo(name="nf_potassium")
    var nf_potassium: Double,
) {
    constructor() : this(0,"",0,0,0, 0.0, "g", 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}