package com.example.personalfinancemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
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

    val amount: Double,

    val month: String, // Stored as "YYYY-MM"

    @ColumnInfo(name = "sync_status")
    var syncStatus: String = "unsynced",

    @ColumnInfo(name = "last_modified")
    var lastModified: String = System.currentTimeMillis().toString()
)