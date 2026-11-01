package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicineWithDoses
import com.example.reminder.MedicineAlarmScheduler
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AddEditMedicineDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicinesScreen
import com.example.ui.screens.ProfilesDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SoundSettingsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.MedicineReminderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val language by viewModel.language.collectAsStateWithLifecycle()

            // Request Notification Permission on Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            MedicineReminderTheme(darkTheme = isDarkTheme) {
                val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    MedicineReminderApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MedicineReminderApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val todayDoses by viewModel.todayDoses.collectAsStateWithLifecycle()
    val stats by viewModel.todayStats.collectAsStateWithLifecycle()
    val historyLogs by viewModel.historyLogs.collectAsStateWithLifecycle()
    val historyFilter by viewModel.historyMedicineFilter.collectAsStateWithLifecycle()
    val currentSound by viewModel.defaultSound.collectAsStateWithLifecycle()
    val playingSound by viewModel.playingSoundName.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isVibration by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingMedicine by remember { mutableStateOf<MedicineWithDoses?>(null) }
    var showProfilesDialog by remember { mutableStateOf(false) }

    // First run seed check
    LaunchedEffect(activeProfile) {
        val prof = activeProfile
        if (prof != null && medicines.isEmpty()) {
            viewModel.saveMedicine(
                existingMedicine = null,
                name = "استامینوفن (Acetaminophen)",
                dosage = "1 عدد (500 mg)",
                form = "Tablet",
                instructions = "بعد از غذا همراه با آب فراوان",
                startDate = selectedDate,
                endDate = null,
                colorHex = "#2D5A43",
                soundName = "Gentle",
                times = listOf("08:00", "14:00", "22:00")
            )
            viewModel.saveMedicine(
                existingMedicine = null,
                name = "امپرازول (Omeprazole)",
                dosage = "1 کپسول (20 mg)",
                form = "Capsule",
                instructions = "صبح ناشتا ۳۰ دقیقه قبل از صبحانه",
                startDate = selectedDate,
                endDate = null,
                colorHex = "#D96B27",
                soundName = "Soft Bell",
                times = listOf("08:00")
            )
        }
    }

    // Predictive Back Handling
    BackHandler(enabled = currentScreen != Screen.HOME) {
        if (currentScreen == Screen.SOUND_SETTINGS) {
            viewModel.navigateTo(Screen.SETTINGS)
        } else {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen != Screen.SOUND_SETTINGS) {
                AppTopBar(
                    language = language,
                    activeProfile = activeProfile,
                    onProfileClick = { showProfilesDialog = true },
                    onAddMedicineClick = {
                        editingMedicine = null
                        showAddEditDialog = true
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.SOUND_SETTINGS) {
                AppBottomNavBar(
                    currentScreen = currentScreen,
                    language = language,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    Screen.HOME -> HomeScreen(
                        language = language,
                        stats = stats,
                        todayDoses = todayDoses,
                        medicines = medicines,
                        onAddMedicineClick = {
                            editingMedicine = null
                            showAddEditDialog = true
                        },
                        onMarkDose = { item, status -> viewModel.markDose(item, status) },
                        onUndoDose = { item -> viewModel.undoDose(item) }
                    )

                    Screen.TODAY -> TodayScreen(
                        language = language,
                        selectedDate = selectedDate,
                        stats = stats,
                        todayDoses = todayDoses,
                        onDateOffset = { offset -> viewModel.setSelectedDateOffset(offset) },
                        onResetDateToToday = { viewModel.resetSelectedDateToToday() },
                        onMarkDose = { item, status -> viewModel.markDose(item, status) },
                        onUndoDose = { item -> viewModel.undoDose(item) }
                    )

                    Screen.MEDICINES -> MedicinesScreen(
                        language = language,
                        medicines = medicines,
                        onAddMedicineClick = {
                            editingMedicine = null
                            showAddEditDialog = true
                        },
                        onEditMedicine = { medWithDoses ->
                            editingMedicine = medWithDoses
                            showAddEditDialog = true
                        },
                        onDeleteMedicine = { med -> viewModel.deleteMedicine(med) }
                    )

                    Screen.HISTORY -> HistoryScreen(
                        language = language,
                        logs = historyLogs,
                        medicines = medicines,
                        selectedMedicineFilter = historyFilter,
                        onFilterMedicine = { id -> viewModel.setHistoryMedicineFilter(id) }
                    )

                    Screen.SETTINGS -> SettingsScreen(
                        language = language,
                        currentSound = currentSound,
                        activeProfile = activeProfile,
                        isDarkTheme = isDarkTheme,
                        isVibrationEnabled = isVibration,
                        onNavigateToSound = { viewModel.navigateTo(Screen.SOUND_SETTINGS) },
                        onOpenProfiles = { showProfilesDialog = true },
                        onSwitchLanguage = { lang -> viewModel.switchLanguage(lang) },
                        onToggleDarkTheme = { dark -> viewModel.toggleDarkTheme(dark) },
                        onToggleVibration = { vib -> viewModel.toggleVibration(vib) },
                        onExportBackup = { viewModel.exportBackupJson() },
                        onRestoreBackup = { json -> viewModel.restoreBackupJson(json) }
                    )

                    Screen.SOUND_SETTINGS -> SoundSettingsScreen(
                        language = language,
                        currentSound = currentSound,
                        playingSoundName = playingSound,
                        onSelectSound = { snd -> viewModel.setDefaultSound(snd) },
                        onPlayPreview = { snd -> viewModel.playSoundPreview(snd) },
                        onStopPreview = { viewModel.stopSoundPreview() },
                        onBack = { viewModel.navigateTo(Screen.SETTINGS) }
                    )
                }
            }
        }
    }

    // Add or Edit Medicine Dialog
    if (showAddEditDialog) {
        AddEditMedicineDialog(
            language = language,
            existingItem = editingMedicine,
            onDismiss = {
                showAddEditDialog = false
                editingMedicine = null
            },
            onSave = { name, dosage, form, instructions, startDate, endDate, colorHex, soundName, times ->
                viewModel.saveMedicine(
                    existingMedicine = editingMedicine?.medicine,
                    name = name,
                    dosage = dosage,
                    form = form,
                    instructions = instructions,
                    startDate = startDate,
                    endDate = endDate,
                    colorHex = colorHex,
                    soundName = soundName,
                    times = times
                )
                showAddEditDialog = false
                editingMedicine = null
            }
        )
    }

    // Profiles Management Dialog
    if (showProfilesDialog) {
        ProfilesDialog(
            language = language,
            profiles = allProfiles,
            activeProfile = activeProfile,
            onDismiss = { showProfilesDialog = false },
            onSelectProfile = { prof -> viewModel.switchProfile(prof) },
            onAddProfile = { name, relation, colorHex -> viewModel.addProfile(name, relation, colorHex) },
            onDeleteProfile = { prof -> viewModel.deleteProfile(prof) }
        )
    }
}
