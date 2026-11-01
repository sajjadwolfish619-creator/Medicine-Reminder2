package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MedicationItem
import com.example.data.model.MedicineWithDoses
import com.example.data.repository.MedicationCatalog
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.sound.SoundPlayer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditMedicineDialog(
    language: AppLanguage,
    existingItem: MedicineWithDoses? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        dosage: String,
        form: String,
        instructions: String,
        startDate: String,
        endDate: String?,
        colorHex: String,
        soundName: String,
        times: List<String>
    ) -> Unit
) {
    val context = LocalContext.current
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var name by remember { mutableStateOf(existingItem?.medicine?.name ?: "") }
    var dosage by remember { mutableStateOf(existingItem?.medicine?.dosage ?: "1 عدد") }
    var form by remember { mutableStateOf(existingItem?.medicine?.form ?: "Tablet") }
    var instructions by remember { mutableStateOf(existingItem?.medicine?.instructions ?: "") }
    var startDate by remember { mutableStateOf(existingItem?.medicine?.startDate ?: todayDate) }
    var endDate by remember { mutableStateOf(existingItem?.medicine?.endDate ?: "") }
    var colorHex by remember { mutableStateOf(existingItem?.medicine?.colorHex ?: "#2D5A43") }
    var soundName by remember { mutableStateOf(existingItem?.medicine?.soundName ?: SoundPlayer.SOUND_GENTLE) }

    val times = remember {
        mutableStateListOf<String>().apply {
            if (existingItem != null && existingItem.doseTimes.isNotEmpty()) {
                addAll(existingItem.doseTimes.map { it.time })
            } else {
                addAll(listOf("08:00", "14:00", "22:00"))
            }
        }
    }

    var showCatalogSuggestions by remember { mutableStateOf(false) }
    var catalogMatches by remember { mutableStateOf<List<MedicationItem>>(emptyList()) }

    val forms = listOf(
        "Tablet" to Strings.get(language, "form_tablet"),
        "Capsule" to Strings.get(language, "form_capsule"),
        "Syrup" to Strings.get(language, "form_syrup"),
        "Drops" to Strings.get(language, "form_drops"),
        "Injection" to Strings.get(language, "form_injection"),
        "Inhaler" to Strings.get(language, "form_inhaler"),
        "Ointment" to Strings.get(language, "form_ointment"),
        "Other" to Strings.get(language, "form_other")
    )

    val colors = listOf(
        "#2D5A43", // Sage green
        "#D96B27", // Terracotta
        "#2B5B84", // Ocean blue
        "#8E44AD", // Purple
        "#27AE60", // Emerald
        "#D32F2F"  // Crimson
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (existingItem == null) Strings.get(language, "add_medicine") else Strings.get(language, "edit_medicine"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // Disclaimer Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.get(language, "catalog_disclaimer"),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Medicine Name Input with search suggestions
                item {
                    Column {
                        Text(
                            text = Strings.get(language, "medicine_name"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { query ->
                                name = query
                                if (query.trim().length >= 2) {
                                    catalogMatches = MedicationCatalog.search(query)
                                    showCatalogSuggestions = catalogMatches.isNotEmpty()
                                } else {
                                    showCatalogSuggestions = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_medicine_name"),
                            placeholder = { Text(Strings.get(language, "medicine_name_hint")) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        // Autocomplete Suggestions from Medication Directory
                        if (showCatalogSuggestions) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = Strings.get(language, "catalog_suggestions"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                    catalogMatches.take(5).forEach { medItem ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    name = if (language == AppLanguage.FA) medItem.nameFa else medItem.nameEn
                                                    dosage = medItem.defaultDosage
                                                    form = medItem.form
                                                    instructions = if (language == AppLanguage.FA) medItem.commonInstructionsFa else medItem.commonInstructionsEn
                                                    showCatalogSuggestions = false
                                                }
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${medItem.nameFa} (${medItem.nameEn})",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "${medItem.genericName} • ${medItem.form}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = medItem.defaultDosage,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Prescribed Dosage Input
                item {
                    Column {
                        Text(
                            text = Strings.get(language, "dosage"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_medicine_dosage"),
                            placeholder = { Text(Strings.get(language, "dosage_hint")) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                // Medicine Form Selector Chips
                item {
                    Column {
                        Text(
                            text = Strings.get(language, "form"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            forms.forEach { (formKey, formLabel) ->
                                val isSelected = form == formKey
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { form = formKey },
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = formLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Daily Intake Times (Multiple Times Supported!)
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Strings.get(language, "dose_times"),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = {
                                    val cal = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val formatted = String.format(Locale.US, "%02d:%02d", hourOfDay, minute)
                                            if (!times.contains(formatted)) {
                                                times.add(formatted)
                                                times.sort()
                                            }
                                        },
                                        cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                                modifier = Modifier.testTag("btn_add_time")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = Strings.get(language, "add_time"))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            times.forEach { timeStr ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = Strings.formatTime(timeStr, language),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        if (times.size > 1) {
                                            IconButton(
                                                onClick = { times.remove(timeStr) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Instructions Input
                item {
                    Column {
                        Text(
                            text = Strings.get(language, "instructions"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_medicine_instructions"),
                            placeholder = { Text(Strings.get(language, "instructions_hint")) },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                // Alarm Sound Choice
                item {
                    Column {
                        Text(
                            text = Strings.get(language, "sound_title"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SoundPlayer.soundOptions.forEach { snd ->
                                val isSelected = soundName == snd
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            soundName = snd
                                            SoundPlayer.play(snd)
                                        },
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = snd,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Color Selection
                item {
                    Column {
                        Text(
                            text = "رنگ نشانگر / Color Tag",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            colors.forEach { hex ->
                                val parsedColor = try {
                                    Color(android.graphics.Color.parseColor(hex))
                                } catch (_: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(parsedColor)
                                        .border(
                                            width = if (colorHex == hex) 3.dp else 0.dp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            shape = CircleShape
                                        )
                                        .clickable { colorHex = hex }
                                )
                            }
                        }
                    }
                }

                // Save & Cancel Buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = Strings.get(language, "cancel"))
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onSave(
                                        name,
                                        dosage,
                                        form,
                                        instructions,
                                        startDate,
                                        if (endDate.isBlank()) null else endDate,
                                        colorHex,
                                        soundName,
                                        times.ifEmpty { listOf("08:00") }
                                    )
                                }
                            },
                            enabled = name.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_save_medicine"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = Strings.get(language, "save"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
