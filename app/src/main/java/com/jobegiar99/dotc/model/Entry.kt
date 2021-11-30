package com.jobegiar99.dotc.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = arrayOf(ForeignKey
        (
            entity = Category::class,
            parentColumns = arrayOf("categoryID"),
            childColumns = arrayOf("categoryID"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Entry (
    @PrimaryKey (autoGenerate = true) @ColumnInfo(name = "entryID") val entryID: Int,
    @NonNull @ColumnInfo(name = "entry_name") var entryName: String,
    @NonNull @ColumnInfo(name = "categoryID") var categoryID: Int,
    @NonNull @ColumnInfo(name = "encoded_entry") var encodedEntry: String
)
{
    constructor(entryName: String,category: Int,encodedEntry: String): this(0,entryName,category, encodedEntry)
}