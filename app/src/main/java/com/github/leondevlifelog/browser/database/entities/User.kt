package com.github.leondevlifelog.browser.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity

data class User(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo var username: String, @ColumnInfo var password: String,
                @ColumnInfo var nickname: String)