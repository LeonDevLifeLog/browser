package com.github.leondevlifelog.browser.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.github.leondevlifelog.browser.database.converter.DateTypeConverter
import com.github.leondevlifelog.browser.database.dao.BookMarkDao
import com.github.leondevlifelog.browser.database.dao.HistoryDao
import com.github.leondevlifelog.browser.database.dao.UserDao
import com.github.leondevlifelog.browser.database.entities.BookMark
import com.github.leondevlifelog.browser.database.entities.History
import com.github.leondevlifelog.browser.database.entities.User


@Database(entities = [History::class, BookMark::class, User::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * 获取历史记录DAO
     */
    abstract fun historyDao(): HistoryDao

    /**
     * 获取书签DAO
     */
    abstract fun bookMarkDao(): BookMarkDao

    abstract fun UserDao(): UserDao
}