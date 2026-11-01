package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.model.Medicine
import com.example.data.model.MedicineDoseTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object MedicineAlarmScheduler {

    const val TAG = "MedAlarmScheduler"
    const val ACTION_MEDICINE_ALARM = "com.sajjad.medr.ACTION_MEDICINE_ALARM"
    const val ACTION_MARK_TAKEN = "com.sajjad.medr.ACTION_MARK_TAKEN"
    const val ACTION_MARK_MISSED = "com.sajjad.medr.ACTION_MARK_MISSED"

    const val EXTRA_DOSE_TIME_ID = "extra_dose_time_id"
    const val EXTRA_MEDICINE_ID = "extra_medicine_id"
    const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
    const val EXTRA_DOSAGE = "extra_dosage"
    const val EXTRA_FORM = "extra_form"
    const val EXTRA_TIME = "extra_time"
    const val EXTRA_SOUND_NAME = "extra_sound_name"

    fun scheduleDoseAlarm(
        context: Context,
        medicine: Medicine,
        doseTime: MedicineDoseTime
    ) {
        if (!doseTime.isAlarmEnabled || !medicine.isActive) {
            cancelDoseAlarm(context, doseTime.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val parts = doseTime.time.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time has already passed today, schedule for next occurrence tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val triggerTime = calendar.timeInMillis

        val intent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = ACTION_MEDICINE_ALARM
            putExtra(EXTRA_DOSE_TIME_ID, doseTime.id)
            putExtra(EXTRA_MEDICINE_ID, medicine.id)
            putExtra(EXTRA_MEDICINE_NAME, medicine.name)
            putExtra(EXTRA_DOSAGE, doseTime.dosageOverride ?: medicine.dosage)
            putExtra(EXTRA_FORM, medicine.form)
            putExtra(EXTRA_TIME, doseTime.time)
            putExtra(EXTRA_SOUND_NAME, medicine.soundName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseTime.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for ${medicine.name} at ${doseTime.time} (time: $triggerTime)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm", e)
        }
    }

    fun cancelDoseAlarm(context: Context, doseTimeId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = ACTION_MEDICINE_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            doseTimeId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled alarm for doseTimeId: $doseTimeId")
        }
    }

    fun rescheduleAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val medicines = db.medicineDao().getAllMedicinesWithDosesSync()
                for (item in medicines) {
                    if (!item.medicine.isActive) continue
                    for (doseTime in item.doseTimes) {
                        scheduleDoseAlarm(context, item.medicine, doseTime)
                    }
                }
                Log.d(TAG, "All alarms rescheduled successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling all alarms", e)
            }
        }
    }
}
