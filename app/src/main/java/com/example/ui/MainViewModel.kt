package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MedicineApp
import com.example.data.backup.BackupManager
import com.example.data.model.IntakeLog
import com.example.data.model.MedicationItem
import com.example.data.model.Medicine
import com.example.data.model.MedicineWithDoses
import com.example.data.model.UserProfile
import com.example.data.repository.MedicationCatalog
import com.example.data.repository.MedicineRepository
import com.example.data.repository.TodayDoseItem
import com.example.i18n.AppLanguage
import com.example.reminder.MedicineAlarmScheduler
import com.example.sound.SoundPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class Screen {
    HOME,
    TODAY,
    MEDICINES,
    HISTORY,
    SETTINGS,
    SOUND_SETTINGS
}

data class TodayStats(
    val total: Int = 0,
    val taken: Int = 0,
    val missed: Int = 0,
    val remaining: Int = 0,
    val progress: Float = 0f,
    val nextDose: TodayDoseItem? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicineRepository = (application as MedicineApp).repository
    private val prefs = application.getSharedPreferences("med_reminder_prefs", Context.MODE_PRIVATE)

    // Language
    private val _language = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("pref_language", "fa") ?: "fa")
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Profiles
    val allProfiles: StateFlow<List<UserProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProfile = MutableStateFlow<UserProfile?>(null)
    val activeProfile: StateFlow<UserProfile?> = _activeProfile.asStateFlow()

    // Selected Date for Today Screen
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Medicines for active profile
    private val _medicines = MutableStateFlow<List<MedicineWithDoses>>(emptyList())
    val medicines: StateFlow<List<MedicineWithDoses>> = _medicines.asStateFlow()

    // Today doses & stats
    private val _todayDoses = MutableStateFlow<List<TodayDoseItem>>(emptyList())
    val todayDoses: StateFlow<List<TodayDoseItem>> = _todayDoses.asStateFlow()

    private val _todayStats = MutableStateFlow(TodayStats())
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    // History logs
    private val _historyLogs = MutableStateFlow<List<IntakeLog>>(emptyList())
    val historyLogs: StateFlow<List<IntakeLog>> = _historyLogs.asStateFlow()

    private val _historyMedicineFilter = MutableStateFlow<Long?>(null)
    val historyMedicineFilter: StateFlow<Long?> = _historyMedicineFilter.asStateFlow()

    // Sound
    private val _defaultSound = MutableStateFlow(
        prefs.getString("pref_sound", SoundPlayer.SOUND_GENTLE) ?: SoundPlayer.SOUND_GENTLE
    )
    val defaultSound: StateFlow<String> = _defaultSound.asStateFlow()

    private val _playingSoundName = MutableStateFlow<String?>(null)
    val playingSoundName: StateFlow<String?> = _playingSoundName.asStateFlow()

    // Catalog Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MedicationItem>>(MedicationCatalog.items.take(15))
    val searchResults: StateFlow<List<MedicationItem>> = _searchResults.asStateFlow()

    // Settings
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("pref_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.getBoolean("pref_vibration", true))
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            val defaultProf = repository.ensureDefaultProfile()
            val savedProfileId = prefs.getLong("pref_active_profile_id", -1L)
            if (savedProfileId != -1L) {
                val found = repository.getProfileById(savedProfileId)
                _activeProfile.value = found ?: defaultProf
            } else {
                _activeProfile.value = defaultProf
            }

            // Observe active profile and update flows
            activeProfile.collect { profile ->
                if (profile != null) {
                    observeMedicinesForProfile(profile.id)
                    observeTodayDoses(profile.id, _selectedDate.value)
                    observeHistoryLogs(profile.id)
                }
            }
        }
    }

    private fun observeMedicinesForProfile(profileId: Long) {
        viewModelScope.launch {
            repository.getMedicinesForProfile(profileId).collect { meds ->
                _medicines.value = meds
            }
        }
    }

    private fun observeTodayDoses(profileId: Long, dateStr: String) {
        viewModelScope.launch {
            repository.getTodayDoses(profileId, dateStr).collect { doses ->
                _todayDoses.value = doses

                val total = doses.size
                val taken = doses.count { it.status == "TAKEN" }
                val missed = doses.count { it.status == "MISSED" }
                val remaining = doses.count { it.status == "PENDING" }
                val progress = if (total > 0) taken.toFloat() / total.toFloat() else 0f

                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val todayStr = dateFormat.format(Date())

                val nextDose = if (dateStr == todayStr) {
                    doses.firstOrNull { it.status == "PENDING" && it.scheduledTime >= currentTime }
                        ?: doses.firstOrNull { it.status == "PENDING" }
                } else {
                    doses.firstOrNull { it.status == "PENDING" }
                }

                _todayStats.value = TodayStats(
                    total = total,
                    taken = taken,
                    missed = missed,
                    remaining = remaining,
                    progress = progress,
                    nextDose = nextDose
                )
            }
        }
    }

    private fun observeHistoryLogs(profileId: Long) {
        viewModelScope.launch {
            repository.getAllLogsForProfile(profileId).collect { logs ->
                _historyLogs.value = logs
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun switchLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("pref_language", lang.code).apply()
    }

    fun switchProfile(profile: UserProfile) {
        _activeProfile.value = profile
        prefs.edit().putLong("pref_active_profile_id", profile.id).apply()
        observeMedicinesForProfile(profile.id)
        observeTodayDoses(profile.id, _selectedDate.value)
        observeHistoryLogs(profile.id)
    }

    fun addProfile(name: String, relation: String, colorHex: String) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                name = name.trim(),
                relation = relation,
                colorHex = colorHex
            )
            val id = repository.saveProfile(newProfile)
            val created = repository.getProfileById(id)
            if (created != null) {
                switchProfile(created)
            }
        }
    }

    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            val remaining = repository.ensureDefaultProfile()
            switchProfile(remaining)
        }
    }

    fun setSelectedDateOffset(offsetDays: Int) {
        val cal = Calendar.getInstance()
        cal.time = dateFormat.parse(_selectedDate.value) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        val newDateStr = dateFormat.format(cal.time)
        _selectedDate.value = newDateStr
        val profile = _activeProfile.value ?: return
        observeTodayDoses(profile.id, newDateStr)
    }

    fun resetSelectedDateToToday() {
        val todayStr = dateFormat.format(Date())
        _selectedDate.value = todayStr
        val profile = _activeProfile.value ?: return
        observeTodayDoses(profile.id, todayStr)
    }

    fun saveMedicine(
        existingMedicine: Medicine? = null,
        name: String,
        dosage: String,
        form: String,
        instructions: String,
        startDate: String,
        endDate: String?,
        colorHex: String,
        soundName: String,
        times: List<String>
    ) {
        val profile = _activeProfile.value ?: return
        viewModelScope.launch {
            val medicine = existingMedicine?.copy(
                name = name.trim(),
                dosage = dosage.trim(),
                form = form,
                instructions = instructions.trim(),
                startDate = startDate,
                endDate = endDate,
                colorHex = colorHex,
                soundName = soundName
            ) ?: Medicine(
                profileId = profile.id,
                name = name.trim(),
                dosage = dosage.trim(),
                form = form,
                instructions = instructions.trim(),
                startDate = startDate,
                endDate = endDate,
                colorHex = colorHex,
                soundName = soundName
            )

            val medId = repository.saveMedicineWithDoses(medicine, times)
            val updatedWithDoses = repository.getMedicineWithDosesById(medId)

            if (updatedWithDoses != null) {
                // Schedule alarms for all times
                val context = getApplication<Application>()
                for (dt in updatedWithDoses.doseTimes) {
                    MedicineAlarmScheduler.scheduleDoseAlarm(context, updatedWithDoses.medicine, dt)
                }
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val withDoses = repository.getMedicineWithDosesById(medicine.id)
            withDoses?.doseTimes?.forEach { dt ->
                MedicineAlarmScheduler.cancelDoseAlarm(context, dt.id)
            }
            repository.deleteMedicine(medicine)
        }
    }

    fun markDose(doseItem: TodayDoseItem, status: String) {
        val profile = _activeProfile.value ?: return
        viewModelScope.launch {
            repository.markDose(
                profileId = profile.id,
                medicine = doseItem.medicine,
                doseTime = doseItem.doseTime,
                dateStr = doseItem.scheduledDate,
                status = status
            )
        }
    }

    fun undoDose(doseItem: TodayDoseItem) {
        val profile = _activeProfile.value ?: return
        viewModelScope.launch {
            repository.undoDose(
                profileId = profile.id,
                medicineId = doseItem.medicine.id,
                doseTimeId = doseItem.doseTime.id,
                dateStr = doseItem.scheduledDate
            )
        }
    }

    fun searchMedication(query: String) {
        _searchQuery.value = query
        _searchResults.value = MedicationCatalog.search(query)
    }

    fun playSoundPreview(soundName: String) {
        _playingSoundName.value = soundName
        SoundPlayer.play(soundName) {
            _playingSoundName.value = null
        }
    }

    fun stopSoundPreview() {
        SoundPlayer.stop()
        _playingSoundName.value = null
    }

    fun setDefaultSound(soundName: String) {
        _defaultSound.value = soundName
        prefs.edit().putString("pref_sound", soundName).apply()
    }

    fun toggleDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        prefs.edit().putBoolean("pref_dark_theme", isDark).apply()
    }

    fun toggleVibration(isEnabled: Boolean) {
        _isVibrationEnabled.value = isEnabled
        prefs.edit().putBoolean("pref_vibration", isEnabled).apply()
    }

    fun setHistoryMedicineFilter(medicineId: Long?) {
        _historyMedicineFilter.value = medicineId
    }

    suspend fun exportBackupJson(): String {
        return BackupManager.createBackupJson(getApplication())
    }

    suspend fun restoreBackupJson(jsonString: String): Result<Int> {
        val result = BackupManager.restoreBackupJson(getApplication(), jsonString)
        if (result.isSuccess) {
            val defProfile = repository.ensureDefaultProfile()
            switchProfile(defProfile)
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        SoundPlayer.stop()
    }
}
