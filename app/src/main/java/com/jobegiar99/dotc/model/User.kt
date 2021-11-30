package com.jobegiar99.dotc.model

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "User")
data class User(

        @PrimaryKey @NonNull @ColumnInfo(name="username") var username: String,
        @NonNull @ColumnInfo(name="password") var password:String
    )
