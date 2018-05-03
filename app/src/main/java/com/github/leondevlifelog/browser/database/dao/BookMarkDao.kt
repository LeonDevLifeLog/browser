package com.github.leondevlifelog.browser.database.dao

import android.arch.persistence.room.*
import com.github.leondevlifelog.browser.database.entities.BookMark

@Dao
interface BookMarkDao {
    @Insert
    fun insert(x: BookMark)

    @Delete
    fun delete(x: BookMark)

    @Query("select * from bookmark")
    fun listAll(): MutableList<BookMark>

    @Update
    fun update(x: BookMark)
}