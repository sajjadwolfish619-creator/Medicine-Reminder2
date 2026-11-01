package com.example.data.model

data class MedicationItem(
    val nameEn: String,
    val nameFa: String,
    val genericName: String,
    val form: String,
    val defaultDosage: String,
    val category: String,
    val commonInstructionsEn: String,
    val commonInstructionsFa: String
)
