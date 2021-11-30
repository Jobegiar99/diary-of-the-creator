package com.jobegiar99.dotc.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Category",
    foreignKeys = arrayOf(
        ForeignKey
        (
            entity = Game::class,
            parentColumns = arrayOf("gameID"),
            childColumns = arrayOf("gameID"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    )
)
data class Category (
    @PrimaryKey (autoGenerate = true) val categoryID: Int,
    @NonNull @ColumnInfo(name = "category_title") var categoryTitle: String,
    @NonNull @ColumnInfo(name = "gameID") var gameID: Int,
    @NonNull @ColumnInfo( name = "encoded_category") var encodedCategory: String
    )
{
    constructor(categoryTitle: String, game: Int, encodedCategory: String): this(0, categoryTitle, game, encodedCategory)
}