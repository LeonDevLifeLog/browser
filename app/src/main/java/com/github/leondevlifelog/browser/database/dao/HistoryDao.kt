package com.github.leondevlifelog.browser.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import com.github.leondevlifelog.browser.database.entities.History

@Dao
interface HistoryDao {
    @Insert
    fun insert(x: History)
}