package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SavingsGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSavingsGoal(savingsGoal: SavingsGoal)

    @Query("SELECT * FROM savings_goals WHERE user_id = :userId ORDER BY goal_name ASC")
    suspend fun getSavingsGoalsForUser(userId: Int): List<SavingsGoal>

    @Delete
    suspend fun deleteSavingsGoal(savingsGoal: SavingsGoal)
}