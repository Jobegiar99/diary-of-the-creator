package com.jobegiar99.dotc.dao

import androidx.room.*
import com.jobegiar99.dotc.model.Game

@Dao
interface GameDAO {

    @Query("SELECT * FROM Game WHERE owner = :username")
    fun getGames(username: String): List<Game>

    @Query("SELECT game_title FROM game WHERE owner = :username")
    fun getGameTitles(username: String): List<String>

    @Query("SELECT * FROM game WHERE owner = :username AND game_title = :game")
    fun getGame(username: String, game: String): Game

    @Query("SELECT * FROM game WHERE gameID = :gameID")
    fun getGameByID(gameID:Int):Game

    @Update
    fun updateGame(game: Game)

    @Insert
    fun insertGame( game: Game)

    @Delete
    fun deleteGame( game: Game)

}