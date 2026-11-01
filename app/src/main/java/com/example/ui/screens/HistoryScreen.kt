package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IntakeLog
import com.example.data.model.MedicineWithDoses
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.ui.theme.MintOnTakenContainer
import com.example.ui.theme.MintTakenContainer
import com.example.ui.theme.MintTakenGreen
import com.example.ui.theme.MissedContainer
import com.example.ui.theme.MissedOnContainer
import com.example.ui.theme.MissedRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    language: AppLanguage,
    logs: List<IntakeLog>,
    medicines: List<MedicineWithDoses>,
    selectedMedicineFilter: Long?,
    onFilterMedicine: (Long?) -> Unit
) {
    val filteredLogs = remember(logs, selectedMedicineFilter) {
        if (selectedMedicineFilter == null) logs
        else logs.filter { it.medicineId == selectedMedicineFilter }
    }

    val totalLogged = filteredLogs.size
    val takenCount = filteredLogs.count { it.status == "TAKEN" }
    val missedCount = filteredLogs.count { it.status == "MISSED" }
    val adherencePercent = if (totalLogged > 0) (takenCount.toFloat() / totalLogged.toFloat() * 100).toInt() else 100

    // Group logs by date
    val groupedByDate = remember(filteredLogs) {
        filteredLogs.groupBy { it.scheduledDate }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Title
        item {
            Text(
                text = Strings.get(language, "history_title"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Adherence Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { if (totalLogged > 0) takenCount.toFloat() / totalLogged.toFloat() else 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = MintTakenGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "${Strings.formatNumber(adherencePercent, language)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get(language, "adherence_rate"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column {
                                Text(
                                    text = Strings.get(language, "stats_taken"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MintTakenGreen
                                )
                                Text(
                                    text = Strings.formatNumber(takenCount, language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MintTakenGreen
                                )
                            }
                            Column {
                                Text(
                                    text = Strings.get(language, "stats_missed"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MissedRed
                                )
                                Text(
                                    text = Strings.formatNumber(missedCount, language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MissedRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Medicine Filters Row
        item {
            Column {
                Text(
                    text = "فیلتر بر اساس دارو / Filter by Medicine",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isAllSelected = selectedMedicineFilter == null
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFilterMedicine(null) },
                            color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = Strings.get(language, "filter_all"),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                    items(medicines) { item ->
                        val isSelected = selectedMedicineFilter == item.medicine.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFilterMedicine(item.medicine.id) },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = item.medicine.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        if (groupedByDate.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Strings.get(language, "no_history_records"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            groupedByDate.forEach { (dateStr, dateLogs) ->
                item {
                    Text(
                        text = "📅 $dateStr",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(dateLogs, key = { it.id }) { log ->
                    val isTaken = log.status == "TAKEN"
                    val loggedTimeFormatted = log.loggedTimestamp?.let {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_item_${log.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTaken) MintTakenContainer.copy(alpha = 0.5f) else MissedContainer.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isTaken) MintTakenGreen else MissedRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isTaken) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.medicineName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${log.dosage} (${log.medicineForm})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "زمان برنامه‌ریزی: ${Strings.formatTime(log.scheduledTime, language)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                if (loggedTimeFormatted != null) {
                                    Text(
                                        text = "ساعت ثبت: ${Strings.formatTime(loggedTimeFormatted, language)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isTaken) MintTakenGreen else MissedRed
                                    )
                                }
                            }

                            Surface(
                                color = if (isTaken) MintTakenGreen else MissedRed,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isTaken) Strings.get(language, "status_taken") else Strings.get(language, "status_missed"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
