package com.example.data.backup

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.IntakeLog
import com.example.data.model.Medicine
import com.example.data.model.MedicineDoseTime
import com.example.data.model.UserProfile
import com.example.reminder.MedicineAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    suspend fun createBackupJson(context: Context): String = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        val dao = db.medicineDao()

        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Medicine Reminder")
        root.put("exportTimestamp", System.currentTimeMillis())

        // Profiles
        val profiles = dao.getAllProfilesSync()
        val profilesArray = JSONArray()
        for (p in profiles) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("relation", p.relation)
                put("colorHex", p.colorHex)
                put("isDefault", p.isDefault)
                put("createdAt", p.createdAt)
            }
            profilesArray.put(obj)
        }
        root.put("profiles", profilesArray)

        // Medicines with doses
        val medsWithDoses = dao.getAllMedicinesWithDosesSync()
        val medsArray = JSONArray()
        for (item in medsWithDoses) {
            val m = item.medicine
            val medObj = JSONObject().apply {
                put("id", m.id)
                put("profileId", m.profileId)
                put("name", m.name)
                put("dosage", m.dosage)
                put("form", m.form)
                put("instructions", m.instructions)
                put("startDate", m.startDate)
                put("endDate", m.endDate ?: JSONObject.NULL)
                put("colorHex", m.colorHex)
                put("soundName", m.soundName)
                put("isActive", m.isActive)
                put("notes", m.notes)
                put("createdAt", m.createdAt)

                val dosesArray = JSONArray()
                for (dt in item.doseTimes) {
                    val dtObj = JSONObject().apply {
                        put("id", dt.id)
                        put("time", dt.time)
                        put("dosageOverride", dt.dosageOverride ?: JSONObject.NULL)
                        put("isAlarmEnabled", dt.isAlarmEnabled)
                    }
                    dosesArray.put(dtObj)
                }
                put("doseTimes", dosesArray)
            }
            medsArray.put(medObj)
        }
        root.put("medicines", medsArray)

        // Logs
        val logs = dao.getAllLogsSync()
        val logsArray = JSONArray()
        for (l in logs) {
            val logObj = JSONObject().apply {
                put("profileId", l.profileId)
                put("medicineId", l.medicineId)
                put("doseTimeId", l.doseTimeId)
                put("medicineName", l.medicineName)
                put("dosage", l.dosage)
                put("medicineForm", l.medicineForm)
                put("scheduledDate", l.scheduledDate)
                put("scheduledTime", l.scheduledTime)
                put("status", l.status)
                put("loggedTimestamp", l.loggedTimestamp ?: JSONObject.NULL)
                put("notes", l.notes ?: JSONObject.NULL)
            }
            logsArray.put(logObj)
        }
        root.put("intakeLogs", logsArray)

        root.toString(2)
    }

    suspend fun restoreBackupJson(context: Context, jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("medicines") || !root.has("profiles")) {
                return@withContext Result.failure(IllegalArgumentException("Invalid Medicine Reminder backup file format"))
            }

            val db = AppDatabase.getInstance(context)
            val dao = db.medicineDao()

            // Map old profile IDs to new IDs
            val oldToNewProfileId = mutableMapOf<Long, Long>()
            val profilesArray = root.getJSONArray("profiles")
            for (i in 0 until profilesArray.length()) {
                val pObj = profilesArray.getJSONObject(i)
                val oldId = pObj.getLong("id")
                val name = pObj.getString("name")
                val relation = pObj.optString("relation", "Self")
                val colorHex = pObj.optString("colorHex", "#2D5A43")
                val isDefault = pObj.optBoolean("isDefault", false)
                val createdAt = pObj.optLong("createdAt", System.currentTimeMillis())

                val newId = dao.insertProfile(
                    UserProfile(
                        name = name,
                        relation = relation,
                        colorHex = colorHex,
                        isDefault = isDefault,
                        createdAt = createdAt
                    )
                )
                oldToNewProfileId[oldId] = newId
            }

            var restoredMedCount = 0
            val oldToNewDoseId = mutableMapOf<Long, Long>()
            val oldToNewMedId = mutableMapOf<Long, Long>()

            val medsArray = root.getJSONArray("medicines")
            for (i in 0 until medsArray.length()) {
                val mObj = medsArray.getJSONObject(i)
                val oldMedId = mObj.getLong("id")
                val oldProfileId = mObj.getLong("profileId")
                val newProfileId = oldToNewProfileId[oldProfileId] ?: oldToNewProfileId.values.firstOrNull() ?: 1L

                val med = Medicine(
                    profileId = newProfileId,
                    name = mObj.getString("name"),
                    dosage = mObj.optString("dosage", "1 Dose"),
                    form = mObj.optString("form", "Tablet"),
                    instructions = mObj.optString("instructions", ""),
                    startDate = mObj.optString("startDate", "2026-01-01"),
                    endDate = if (mObj.isNull("endDate")) null else mObj.getString("endDate"),
                    colorHex = mObj.optString("colorHex", "#2D5A43"),
                    soundName = mObj.optString("soundName", "Gentle"),
                    isActive = mObj.optBoolean("isActive", true),
                    notes = mObj.optString("notes", ""),
                    createdAt = mObj.optLong("createdAt", System.currentTimeMillis())
                )

                val newMedId = dao.insertMedicine(med)
                oldToNewMedId[oldMedId] = newMedId
                restoredMedCount++

                if (mObj.has("doseTimes")) {
                    val dtArray = mObj.getJSONArray("doseTimes")
                    for (j in 0 until dtArray.length()) {
                        val dtObj = dtArray.getJSONObject(j)
                        val oldDtId = dtObj.getLong("id")
                        val time = dtObj.getString("time")
                        val override = if (dtObj.isNull("dosageOverride")) null else dtObj.getString("dosageOverride")
                        val enabled = dtObj.optBoolean("isAlarmEnabled", true)

                        val newDtId = dao.insertDoseTime(
                            MedicineDoseTime(
                                medicineId = newMedId,
                                time = time,
                                dosageOverride = override,
                                isAlarmEnabled = enabled
                            )
                        )
                        oldToNewDoseId[oldDtId] = newDtId
                    }
                }
            }

            if (root.has("intakeLogs")) {
                val logsArray = root.getJSONArray("intakeLogs")
                for (k in 0 until logsArray.length()) {
                    val lObj = logsArray.getJSONObject(k)
                    val oldProfileId = lObj.getLong("profileId")
                    val oldMedId = lObj.getLong("medicineId")
                    val oldDtId = lObj.getLong("doseTimeId")

                    val newProfileId = oldToNewProfileId[oldProfileId] ?: continue
                    val newMedId = oldToNewMedId[oldMedId] ?: continue
                    val newDtId = oldToNewDoseId[oldDtId] ?: continue

                    val log = IntakeLog(
                        profileId = newProfileId,
                        medicineId = newMedId,
                        doseTimeId = newDtId,
                        medicineName = lObj.getString("medicineName"),
                        dosage = lObj.getString("dosage"),
                        medicineForm = lObj.optString("medicineForm", "Tablet"),
                        scheduledDate = lObj.getString("scheduledDate"),
                        scheduledTime = lObj.getString("scheduledTime"),
                        status = lObj.getString("status"),
                        loggedTimestamp = if (lObj.isNull("loggedTimestamp")) null else lObj.getLong("loggedTimestamp"),
                        notes = if (lObj.isNull("notes")) null else lObj.getString("notes")
                    )
                    dao.insertLog(log)
                }
            }

            // Reschedule all alarms for restored medicines
            MedicineAlarmScheduler.rescheduleAll(context)

            Result.success(restoredMedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
