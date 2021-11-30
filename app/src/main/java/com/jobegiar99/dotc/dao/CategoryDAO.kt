package com.jobegiar99.dotc.dao

import androidx.room.*
import com.jobegiar99.dotc.model.Category

@Dao
interface CategoryDAO {

    @Query("SELECT category_title FROM Category WHERE gameID = :gameID")
    fun getCategories(gameID:Int): List<String>

    @Query("SELECT * from Category WHERE category_title = :categoryTitle AND gameID = :gameID")
    fun getCategory(categoryTitle:String, gameID: Int): Category

    @Query("SELECT * FROM category WHERE gameID = :gameID")
    fun getCategoryByID(gameID: Int): Category

    @Query("SELECT * FROM category WHERE categoryID = :categoryID")
    fun getCategoryByCID(categoryID: Int): Category


    @Update
    fun updateCategory(category:Category)

    @Insert
    fun insertCategory(category:Category)

    @Delete
    fun deleteCategory(category:Category)

}