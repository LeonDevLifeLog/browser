package com.github.leondevlifelog.browser.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.github.leondevlifelog.browser.database.entities.BookMark

@Dao
interface BookMarkDao {
    @Insert
    fun insert(x: BookMark)

    @Delete
    fun delete(x: BookMark)

    @Query("select * from bookmark")
    fun listAll(): List<BookMark>
}