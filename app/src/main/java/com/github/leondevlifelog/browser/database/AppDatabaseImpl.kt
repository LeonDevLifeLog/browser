package com.github.leondevlifelog.browser.database

import android.arch.persistence.room.Room
import com.github.leondevlifelog.browser.App


class AppDatabaseImpl {
    companion object {
        val instance: AppDatabase by lazy {
            Room.databaseBuilder(App.instance,
                    AppDatabase::class.java, "database")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}