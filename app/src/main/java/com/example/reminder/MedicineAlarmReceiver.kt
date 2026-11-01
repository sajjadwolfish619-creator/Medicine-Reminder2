package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.db.AppDatabase
import com.example.data.repository.MedicineRepository
import com.example.sound.SoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "medicine_reminder_channel"
        const val CHANNEL_NAME = "Medicine Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val doseTimeId = intent.getLongExtra(MedicineAlarmScheduler.EXTRA_DOSE_TIME_ID, -1L)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (action) {
            MedicineAlarmScheduler.ACTION_MEDICINE_ALARM -> {
                val medicineId = intent.getLongExtra(MedicineAlarmScheduler.EXTRA_MEDICINE_ID, -1L)
                val medicineName = intent.getStringExtra(MedicineAlarmScheduler.EXTRA_MEDICINE_NAME) ?: "Medicine"
                val dosage = intent.getStringExtra(MedicineAlarmScheduler.EXTRA_DOSAGE) ?: ""
                val form = intent.getStringExtra(MedicineAlarmScheduler.EXTRA_FORM) ?: "Dose"
                val time = intent.getStringExtra(MedicineAlarmScheduler.EXTRA_TIME) ?: ""
                val soundName = intent.getStringExtra(MedicineAlarmScheduler.EXTRA_SOUND_NAME) ?: "Gentle"

                showAlarmNotification(
                    context,
                    notificationManager,
                    doseTimeId,
                    medicineId,
                    medicineName,
                    dosage,
                    form,
                    time
                )

                // Play synthesized alarm sound
                SoundPlayer.play(soundName)

                // Reschedule for next day occurrence
                rescheduleNextDay(context, doseTimeId)
            }

            MedicineAlarmScheduler.ACTION_MARK_TAKEN -> {
                SoundPlayer.stop()
                if (doseTimeId != -1L) {
                    notificationManager.cancel(doseTimeId.toInt())
                    val db = AppDatabase.getInstance(context)
                    val repo = MedicineRepository(db.medicineDao())
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.markDoseByTimeId(doseTimeId, "TAKEN")
                    }
                }
            }

            MedicineAlarmScheduler.ACTION_MARK_MISSED -> {
                SoundPlayer.stop()
                if (doseTimeId != -1L) {
                    notificationManager.cancel(doseTimeId.toInt())
                    val db = AppDatabase.getInstance(context)
                    val repo = MedicineRepository(db.medicineDao())
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.markDoseByTimeId(doseTimeId, "MISSED")
                    }
                }
            }
        }
    }

    private fun showAlarmNotification(
        context: Context,
        manager: NotificationManager,
        doseTimeId: Long,
        medicineId: Long,
        medicineName: String,
        dosage: String,
        form: String,
        time: String
    ) {
        createNotificationChannel(manager)

        // Launch app on notification click
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_DOSE_ID", doseTimeId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            doseTimeId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Taken" action button
        val takenIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = MedicineAlarmScheduler.ACTION_MARK_TAKEN
            putExtra(MedicineAlarmScheduler.EXTRA_DOSE_TIME_ID, doseTimeId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            (doseTimeId * 10 + 1).toInt(),
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "Missed" action button
        val missedIntent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            action = MedicineAlarmScheduler.ACTION_MARK_MISSED
            putExtra(MedicineAlarmScheduler.EXTRA_DOSE_TIME_ID, doseTimeId)
        }
        val missedPendingIntent = PendingIntent.getBroadcast(
            context,
            (doseTimeId * 10 + 2).toInt(),
            missedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("💊 یادآور مصرف دارو: $medicineName")
            .setContentText("زمان مصرف: $time | مقدار: $dosage")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("نوبت مصرف $medicineName ($dosage - $form) فرا رسیده است.\nساعت مقرر: $time")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "مصرف شد (Taken)", takenPendingIntent)
            .addAction(android.R.drawable.ic_delete, "مصرف نشد (Missed)", missedPendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .build()

        manager.notify(doseTimeId.toInt(), notification)
    }

    private fun rescheduleNextDay(context: Context, doseTimeId: Long) {
        if (doseTimeId == -1L) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val doseTime = db.medicineDao().getDoseTimeById(doseTimeId) ?: return@launch
                val medicine = db.medicineDao().getMedicineById(doseTime.medicineId) ?: return@launch
                MedicineAlarmScheduler.scheduleDoseAlarm(context, medicine, doseTime)
            } catch (_: Exception) {}
        }
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent notifications for scheduled medicine intake"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            }
            manager.createNotificationChannel(channel)
        }
    }
}
