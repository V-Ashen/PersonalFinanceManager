package com.example.personalfinancemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // This must match your table name
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0, // Let Room handle generating IDs
    val username: String,
    val email: String
)