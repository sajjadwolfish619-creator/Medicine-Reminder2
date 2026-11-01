package com.example.data.repository

import com.example.data.db.MedicineDao
import com.example.data.model.IntakeLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineDoseTime
import com.example.data.model.MedicineWithDoses
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TodayDoseItem(
    val medicine: Medicine,
    val doseTime: MedicineDoseTime,
    val scheduledDate: String,
    val scheduledTime: String,
    val status: String, // PENDING, TAKEN, MISSED
    val loggedTimestamp: Long? = null,
    val logId: Long? = null
)

class MedicineRepository(private val dao: MedicineDao) {

    val allProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()

    suspend fun ensureDefaultProfile(): UserProfile = withContext(Dispatchers.IO) {
        val existing = dao.getAllProfilesSync()
        if (existing.isNotEmpty()) {
            return@withContext existing.first()
        }
        val defaultProfile = UserProfile(
            name = "خودم / Me",
            relation = "Self",
            colorHex = "#2D5A43",
            isDefault = true
        )
        val id = dao.insertProfile(defaultProfile)
        defaultProfile.copy(id = id)
    }

    suspend fun getProfileById(id: Long): UserProfile? = withContext(Dispatchers.IO) {
        dao.getProfileById(id)
    }

    suspend fun saveProfile(profile: UserProfile): Long = withContext(Dispatchers.IO) {
        if (profile.id == 0L) {
            dao.insertProfile(profile)
        } else {
            dao.updateProfile(profile)
            profile.id
        }
    }

    suspend fun deleteProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.deleteProfile(profile)
    }

    fun getMedicinesForProfile(profileId: Long): Flow<List<MedicineWithDoses>> {
        return dao.getMedicinesWithDosesForProfile(profileId)
    }

    suspend fun getAllMedicinesWithDosesSync(): List<MedicineWithDoses> = withContext(Dispatchers.IO) {
        dao.getAllMedicinesWithDosesSync()
    }

    suspend fun getMedicineWithDosesById(id: Long): MedicineWithDoses? = withContext(Dispatchers.IO) {
        dao.getMedicineWithDosesById(id)
    }

    suspend fun saveMedicineWithDoses(
        medicine: Medicine,
        times: List<String>
    ): Long = withContext(Dispatchers.IO) {
        val medicineId = if (medicine.id == 0L) {
            dao.insertMedicine(medicine)
        } else {
            dao.updateMedicine(medicine)
            dao.deleteDoseTimesForMedicine(medicine.id)
            medicine.id
        }

        val doseTimes = times.map { timeStr ->
            MedicineDoseTime(
                medicineId = medicineId,
                time = timeStr.trim(),
                isAlarmEnabled = true
            )
        }
        dao.insertDoseTimes(doseTimes)
        medicineId
    }

    suspend fun deleteMedicine(medicine: Medicine) = withContext(Dispatchers.IO) {
        dao.deleteLogsForMedicine(medicine.id)
        dao.deleteDoseTimesForMedicine(medicine.id)
        dao.deleteMedicine(medicine)
    }

    fun getTodayDoses(profileId: Long, dateStr: String): Flow<List<TodayDoseItem>> {
        val medicinesFlow = dao.getMedicinesWithDosesForProfile(profileId)
        val logsFlow = dao.getLogsForDate(profileId, dateStr)

        return combine(medicinesFlow, logsFlow) { meds, logs ->
            val result = mutableListOf<TodayDoseItem>()
            val logMap = logs.associateBy { it.doseTimeId }

            for (medWithDoses in meds) {
                val med = medWithDoses.medicine
                if (!med.isActive) continue

                // Check date bounds
                if (dateStr < med.startDate) continue
                if (med.endDate != null && dateStr > med.endDate) continue

                for (doseTime in medWithDoses.doseTimes) {
                    val log = logMap[doseTime.id]
                    val status = log?.status ?: "PENDING"
                    val loggedAt = log?.loggedTimestamp
                    val logId = log?.id

                    result.add(
                        TodayDoseItem(
                            medicine = med,
                            doseTime = doseTime,
                            scheduledDate = dateStr,
                            scheduledTime = doseTime.time,
                            status = status,
                            loggedTimestamp = loggedAt,
                            logId = logId
                        )
                    )
                }
            }
            // Sort chronologically by scheduled time
            result.sortedBy { it.scheduledTime }
        }
    }

    suspend fun markDose(
        profileId: Long,
        medicine: Medicine,
        doseTime: MedicineDoseTime,
        dateStr: String,
        status: String // TAKEN or MISSED
    ) = withContext(Dispatchers.IO) {
        val existingLog = dao.getLog(profileId, medicine.id, doseTime.id, dateStr)
        if (existingLog != null) {
            dao.updateLog(
                existingLog.copy(
                    status = status,
                    loggedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            val newLog = IntakeLog(
                profileId = profileId,
                medicineId = medicine.id,
                doseTimeId = doseTime.id,
                medicineName = medicine.name,
                dosage = doseTime.dosageOverride ?: medicine.dosage,
                medicineForm = medicine.form,
                scheduledDate = dateStr,
                scheduledTime = doseTime.time,
                status = status,
                loggedTimestamp = System.currentTimeMillis()
            )
            dao.insertLog(newLog)
        }
    }

    suspend fun undoDose(
        profileId: Long,
        medicineId: Long,
        doseTimeId: Long,
        dateStr: String
    ) = withContext(Dispatchers.IO) {
        val existingLog = dao.getLog(profileId, medicineId, doseTimeId, dateStr)
        if (existingLog != null) {
            dao.deleteLog(existingLog)
        }
    }

    fun getAllLogsForProfile(profileId: Long): Flow<List<IntakeLog>> {
        return dao.getAllLogsForProfile(profileId)
    }

    // Direct access for AlarmReceiver and BootReceiver
    suspend fun markDoseByTimeId(
        doseTimeId: Long,
        status: String
    ) = withContext(Dispatchers.IO) {
        val doseTime = dao.getDoseTimeById(doseTimeId) ?: return@withContext
        val medicine = dao.getMedicineById(doseTime.medicineId) ?: return@withContext
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val existingLog = dao.getLog(medicine.profileId, medicine.id, doseTime.id, todayStr)
        if (existingLog != null) {
            dao.updateLog(
                existingLog.copy(
                    status = status,
                    loggedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertLog(
                IntakeLog(
                    profileId = medicine.profileId,
                    medicineId = medicine.id,
                    doseTimeId = doseTime.id,
                    medicineName = medicine.name,
                    dosage = doseTime.dosageOverride ?: medicine.dosage,
                    medicineForm = medicine.form,
                    scheduledDate = todayStr,
                    scheduledTime = doseTime.time,
                    status = status,
                    loggedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
