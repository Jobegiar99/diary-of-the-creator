package com.jobegiar99.dotc.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Game",
    foreignKeys = arrayOf(
        ForeignKey
            (
                entity = User::class,
                parentColumns = arrayOf("username"),
                childColumns = arrayOf("owner"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
            )
    )
)
class Game (
    @PrimaryKey (autoGenerate = true) val gameID: Int,
    @NonNull @ColumnInfo(name = "game_title") var gameTitle: String,
    @NonNull @ColumnInfo(name = "owner") var owner: String
)
{
    constructor(gameTitle: String, owner: String): this(0,gameTitle,owner)
}