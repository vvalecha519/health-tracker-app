package com.group11.healthtrackerapp.UserInfo

import androidx.room.*

@Dao
interface UserDao {
    @Query("select * from user order by user._id ASC")
    fun getAll(): List<User>

    @Insert
    fun insert(user:User)

    @Query("select * from user order by _id DESC LIMIT 1")
    fun getLastOne():User?

    @Query("select * from user order by _id ASC LIMIT 1")
    fun getFirstOne():User?
}