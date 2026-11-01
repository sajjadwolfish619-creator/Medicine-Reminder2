package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicines",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val name: String,
    val dosage: String, // e.g. "1 Tablet", "500 mg", "5 ml"
    val form: String = "Tablet", // Tablet, Capsule, Syrup, Drops, Injection, Inhaler, Ointment, Other
    val instructions: String = "", // e.g. "After meal", "With water", "Empty stomach"
    val startDate: String, // YYYY-MM-DD
    val endDate: String? = null, // YYYY-MM-DD or null for continuous
    val colorHex: String = "#2D5A43",
    val soundName: String = "Gentle", // Gentle, Classic, Digital, Loud, Soft Bell
    val isActive: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
