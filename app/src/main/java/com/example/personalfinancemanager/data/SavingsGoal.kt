package com.example.personalfinancemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings_goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goal_id")
    val goalId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "goal_name")
    val goalName: String,

    @ColumnInfo(name = "target_amount")
    val targetAmount: Double,

    @ColumnInfo(name = "current_amount")
    val currentAmount: Double = 0.0,

    @ColumnInfo(name = "sync_status")
    var syncStatus: String = "unsynced",

    @ColumnInfo(name = "last_modified")
    var lastModified: String = System.currentTimeMillis().toString()
)