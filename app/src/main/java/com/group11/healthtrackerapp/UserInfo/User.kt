package com.group11.healthtrackerapp.UserInfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var _id: Int,
    @ColumnInfo(name="user_first_name")
    var firstName: String,
    @ColumnInfo(name = "user_last_name")
    var lastName: String,
    @ColumnInfo(name = "user_dob")
    var dateOfBirth: String,
    @ColumnInfo(name = "user_gender")
    var gender: String,
    @ColumnInfo(name = "user_height")
    var height: Int,
    @ColumnInfo(name = "user_weight")
    var weight: Int,
    @ColumnInfo(name = "user_target_weight")
    var targetWeight: Int,
    @ColumnInfo(name="user_current_date")
    var currentDate: String
) {
    constructor() : this(0,"", "", "", "", 0, 0, 0,"")
}