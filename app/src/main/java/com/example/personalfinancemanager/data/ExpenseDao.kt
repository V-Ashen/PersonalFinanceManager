package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE sync_status IN ('unsynced', 'modified')")
    suspend fun getUnsyncedExpenses(): List<Expense>

    @Query("UPDATE expenses SET sync_status = 'synced' WHERE expenseId IN (:ids)")
    suspend fun updateSyncStatus(ids: List<Int>)

    @Query("""
    SELECT e.*, c.name as categoryName
    FROM expenses e
    JOIN categories c ON e.categoryId = c.categoryId
    WHERE e.userId = :userId
    ORDER BY e.expenseId DESC
""")
    suspend fun getAllExpensesWithCategory(userId: Int): List<ExpenseWithCategory>
}