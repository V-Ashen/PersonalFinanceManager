package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudgetDao {
    @Insert
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE user_id = :userId ORDER BY month DESC")
    suspend fun getBudgetsForUser(userId: Int): List<Budget>
}