package com.github.leondevlifelog.browser.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class History(@PrimaryKey var id: Int, @ColumnInfo var title: String,
                   @ColumnInfo var url: String, @ColumnInfo var time: Date)