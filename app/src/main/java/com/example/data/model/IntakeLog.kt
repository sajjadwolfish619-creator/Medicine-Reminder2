package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "intake_logs",
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["medicineId"]),
        Index(value = ["scheduledDate"])
    ]
)
data class IntakeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val medicineId: Long,
    val doseTimeId: Long,
    val medicineName: String,
    val dosage: String,
    val medicineForm: String = "Tablet",
    val scheduledDate: String, // "YYYY-MM-DD"
    val scheduledTime: String, // "HH:mm"
    val status: String, // "TAKEN", "MISSED", "PENDING"
    val loggedTimestamp: Long? = null, // epoch millis when user tapped Taken or Missed
    val notes: String? = null
)
