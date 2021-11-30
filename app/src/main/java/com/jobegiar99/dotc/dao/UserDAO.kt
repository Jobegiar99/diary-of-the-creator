package com.jobegiar99.dotc.dao

import android.provider.ContactsContract
import androidx.room.*
import com.jobegiar99.dotc.model.User

@Dao
interface UserDAO {

    @Query("SELECT * FROM USER WHERE username = :username")
    fun getUser(username: String): User

    @Update
    fun updateUser(user: User)

    @Insert
    fun insertUser(user: User)

    @Delete
    fun deleteUser( user: User)

}