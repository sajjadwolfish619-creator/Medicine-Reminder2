package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.IntakeLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineDoseTime
import com.example.data.model.MedicineWithDoses
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    // --- Profiles ---
    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    suspend fun getAllProfilesSync(): List<UserProfile>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile): Long

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Delete
    suspend fun deleteProfile(profile: UserProfile)

    // --- Medicines ---
    @Transaction
    @Query("SELECT * FROM medicines WHERE profileId = :profileId AND isActive = 1 ORDER BY id DESC")
    fun getMedicinesWithDosesForProfile(profileId: Long): Flow<List<MedicineWithDoses>>

    @Transaction
    @Query("SELECT * FROM medicines ORDER BY id DESC")
    fun getAllMedicinesWithDoses(): Flow<List<MedicineWithDoses>>

    @Transaction
    @Query("SELECT * FROM medicines ORDER BY id DESC")
    suspend fun getAllMedicinesWithDosesSync(): List<MedicineWithDoses>

    @Transaction
    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getMedicineWithDosesById(id: Long): MedicineWithDoses?

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getMedicineById(id: Long): Medicine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    // --- Dose Times ---
    @Query("SELECT * FROM dose_times WHERE medicineId = :medicineId ORDER BY time ASC")
    suspend fun getDoseTimesForMedicine(medicineId: Long): List<MedicineDoseTime>

    @Query("SELECT * FROM dose_times WHERE id = :id LIMIT 1")
    suspend fun getDoseTimeById(id: Long): MedicineDoseTime?

    @Query("SELECT * FROM dose_times")
    suspend fun getAllDoseTimesSync(): List<MedicineDoseTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseTime(doseTime: MedicineDoseTime): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseTimes(doseTimes: List<MedicineDoseTime>): List<Long>

    @Query("DELETE FROM dose_times WHERE medicineId = :medicineId")
    suspend fun deleteDoseTimesForMedicine(medicineId: Long)

    // --- Intake Logs ---
    @Query("SELECT * FROM intake_logs WHERE profileId = :profileId AND scheduledDate = :date ORDER BY scheduledTime ASC")
    fun getLogsForDate(profileId: Long, date: String): Flow<List<IntakeLog>>

    @Query("SELECT * FROM intake_logs WHERE profileId = :profileId ORDER BY scheduledDate DESC, scheduledTime DESC")
    fun getAllLogsForProfile(profileId: Long): Flow<List<IntakeLog>>

    @Query("SELECT * FROM intake_logs ORDER BY scheduledDate DESC, scheduledTime DESC")
    fun getAllLogs(): Flow<List<IntakeLog>>

    @Query("SELECT * FROM intake_logs ORDER BY scheduledDate DESC, scheduledTime DESC")
    suspend fun getAllLogsSync(): List<IntakeLog>

    @Query("SELECT * FROM intake_logs WHERE profileId = :profileId AND medicineId = :medicineId AND doseTimeId = :doseTimeId AND scheduledDate = :date LIMIT 1")
    suspend fun getLog(profileId: Long, medicineId: Long, doseTimeId: Long, date: String): IntakeLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IntakeLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<IntakeLog>): List<Long>

    @Update
    suspend fun updateLog(log: IntakeLog)

    @Delete
    suspend fun deleteLog(log: IntakeLog)

    @Query("DELETE FROM intake_logs WHERE medicineId = :medicineId")
    suspend fun deleteLogsForMedicine(medicineId: Long)

    // Full clean for restore
    @Query("DELETE FROM dose_times")
    suspend fun clearAllDoseTimes()

    @Query("DELETE FROM medicines")
    suspend fun clearAllMedicines()

    @Query("DELETE FROM intake_logs")
    suspend fun clearAllLogs()

    @Query("DELETE FROM user_profiles")
    suspend fun clearAllProfiles()
}
