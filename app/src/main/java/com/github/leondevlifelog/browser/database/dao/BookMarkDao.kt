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

    @Query("select * from bookmark where url like '%' || :find || '%' or title like '%'||:find||'%'")
    fun findByUrl(find: String): MutableList<BookMark>

    @Query("select category from bookmark")
    fun findAllCategory(): MutableList<String>

    @Query("select * from bookmark where category = :x")
    fun findByCategory(x: String): MutableList<BookMark>
}