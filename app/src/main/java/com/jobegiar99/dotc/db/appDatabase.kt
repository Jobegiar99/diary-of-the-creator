package com.jobegiar99.dotc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jobegiar99.dotc.dao.CategoryDAO
import com.jobegiar99.dotc.dao.EntryDAO
import com.jobegiar99.dotc.dao.GameDAO
import com.jobegiar99.dotc.dao.UserDAO
import com.jobegiar99.dotc.model.*

@Database(entities = [Category::class, Entry::class, Game::class, User::class],version = 1)
abstract class appDatabase:RoomDatabase() {

    abstract fun DaoCategory(): CategoryDAO

    abstract  fun DaoEntry(): EntryDAO

    abstract fun DaoGame(): GameDAO

    abstract fun DaoUser(): UserDAO


}