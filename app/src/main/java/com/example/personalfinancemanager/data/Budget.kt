package com.example.personalfinancemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budget_id")
    val budgetId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    val amount: Double,

    val month: String, // Stored as "YYYY-MM"

    @ColumnInfo(name = "sync_status")
    var syncStatus: String = "unsynced",

    @ColumnInfo(name = "last_modified")
    var lastModified: String = System.currentTimeMillis().toString()
)