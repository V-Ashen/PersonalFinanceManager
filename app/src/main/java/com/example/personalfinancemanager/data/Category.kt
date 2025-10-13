package com.example.personalfinancemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val userId: Int, // Foreign key to the User table
    val name: String
)