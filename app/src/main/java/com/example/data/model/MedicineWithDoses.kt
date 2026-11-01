package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class MedicineWithDoses(
    @Embedded val medicine: Medicine,
    @Relation(
        parentColumn = "id",
        entityColumn = "medicineId"
    )
    val doseTimes: List<MedicineDoseTime>
)
