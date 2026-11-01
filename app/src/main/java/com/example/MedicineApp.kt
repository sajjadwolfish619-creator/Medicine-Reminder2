package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.db.AppDatabase
import com.example.data.repository.MedicineRepository
import com.example.reminder.MedicineAlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: MedicineRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = MedicineRepository(database.medicineDao())

        createNotificationChannel()

        // Ensure at least one profile exists
        CoroutineScope(Dispatchers.IO).launch {
            repository.ensureDefaultProfile()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MedicineAlarmReceiver.CHANNEL_ID,
                MedicineAlarmReceiver.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for prescribed medication doses"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
