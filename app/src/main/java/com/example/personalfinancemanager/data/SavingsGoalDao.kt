package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavingsGoalDao {
    @Insert
    suspend fun insertSavingsGoal(savingsGoal: SavingsGoal)

    @Query("SELECT * FROM savings_goals WHERE user_id = :userId ORDER BY goal_name ASC")
    suspend fun getSavingsGoalsForUser(userId: Int): List<SavingsGoal>
}