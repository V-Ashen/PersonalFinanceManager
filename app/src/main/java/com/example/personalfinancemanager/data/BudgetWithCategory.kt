package com.example.personalfinancemanager.data
import androidx.room.Embedded

data class BudgetWithCategory(
    @Embedded
    val budget: Budget,
    val categoryName: String
)