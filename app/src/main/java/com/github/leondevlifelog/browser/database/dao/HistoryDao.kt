package com.github.leondevlifelog.browser.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.github.leondevlifelog.browser.database.entities.History

@Dao
interface HistoryDao {
    @Insert
    fun insert(x: History)

    @Delete
    fun delete(x: History)

    @Query("select * from history")
    fun listHistory(): MutableList<History>
}