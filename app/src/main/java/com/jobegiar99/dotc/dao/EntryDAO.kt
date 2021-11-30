package com.jobegiar99.dotc.dao

import androidx.room.*
import com.jobegiar99.dotc.model.Entry

@Dao
interface EntryDAO {

    @Query("SELECT * FROM Entry WHERE categoryID = :categoryID AND entryID = :entryID")
    fun getEntry(categoryID:Int, entryID: Int): Entry

    @Query("SELECT * FROM Entry WHERE categoryID = :categoryID AND entry_name = :entry_name")
    fun getEntryByName(categoryID:Int, entry_name: String): Entry

    @Query("SELECT * FROM Entry WHERE categoryID = :categoryID AND entryID")
    fun getEntries(categoryID: Int): List<Entry>

    @Query("SELECT entry_name FROM Entry WHERE categoryID = :categoryID")
    fun getEntryTitles(categoryID: Int): List<String>

    @Update
    fun updateEntry(entry:Entry)

    @Insert
    fun insertEntry(entry:Entry)

    @Delete
    fun deleteEntry(entry:Entry)


}