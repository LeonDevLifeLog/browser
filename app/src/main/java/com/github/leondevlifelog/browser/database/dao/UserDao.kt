package com.github.leondevlifelog.browser.database.dao

import android.arch.persistence.room.*
import com.github.leondevlifelog.browser.database.entities.User

@Dao
interface UserDao {
    @Insert
    fun insert(x: User)

    @Delete
    fun delete(x: User)

    @Update
    fun update(x: User)

    @Query("select * from user where username = :username")
    fun findUserByUsername(username: String): User?
}