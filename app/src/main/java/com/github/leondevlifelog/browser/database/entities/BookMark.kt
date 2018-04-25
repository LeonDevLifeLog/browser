package com.github.leondevlifelog.browser.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity
data class BookMark(@PrimaryKey(autoGenerate = true) var id: Int = 0, @ColumnInfo var title: String,
                    @ColumnInfo var url: String, @ColumnInfo var time: Date) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readSerializable() as Date
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeString(url)
        writeSerializable(time)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BookMark> = object : Parcelable.Creator<BookMark> {
            override fun createFromParcel(source: Parcel): BookMark = BookMark(source)
            override fun newArray(size: Int): Array<BookMark?> = arrayOfNulls(size)
        }
    }
}