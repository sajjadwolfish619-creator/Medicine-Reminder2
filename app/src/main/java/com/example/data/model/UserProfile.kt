package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String = "Self", // Self, Mother, Father, Child, Spouse, Other
    val colorHex: String = "#2D5A43",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
