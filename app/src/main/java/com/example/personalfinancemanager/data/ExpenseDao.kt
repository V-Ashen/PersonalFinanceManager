package com.example.personalfinancemanager.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)
}