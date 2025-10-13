package com.example.personalfinancemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "expenses",
    // This section defines the foreign key relationships for Room
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // If a user is deleted, their expenses are too
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // If a category is deleted, its expenses are too
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val expenseDate: String, // Storing as "YYYY-MM-DD" string for simplicity
    val description: String?,

    // These columns are crucial for synchronization later!
    @ColumnInfo(name = "sync_status")
    var syncStatus: String = "unsynced",

    @ColumnInfo(name = "last_modified")
    var lastModified: String = System.currentTimeMillis().toString()
)