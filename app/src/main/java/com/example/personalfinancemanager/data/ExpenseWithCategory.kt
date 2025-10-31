package com.example.personalfinancemanager.data

import androidx.room.Embedded

data class ExpenseWithCategory(
    @Embedded
    val expense: Expense,
    val categoryName: String
)