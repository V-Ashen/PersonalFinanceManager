package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    suspend fun getCategoriesForUser(userId: Int): List<Category>
}