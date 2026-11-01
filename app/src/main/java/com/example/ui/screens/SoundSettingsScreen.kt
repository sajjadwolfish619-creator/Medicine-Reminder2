package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.sound.SoundPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundSettingsScreen(
    language: AppLanguage,
    currentSound: String,
    playingSoundName: String?,
    onSelectSound: (String) -> Unit,
    onPlayPreview: (String) -> Unit,
    onStopPreview: () -> Unit,
    onBack: () -> Unit
) {
    val soundDescriptions = mapOf(
        SoundPlayer.SOUND_GENTLE to if (language == AppLanguage.FA) "تن آرام و تدریجی، مناسب بیدار شدن ملایم" else "Soft, harmonic ascending chime",
        SoundPlayer.SOUND_CLASSIC to if (language == AppLanguage.FA) "ملودی کلاسیک سنتی و موزون ساعت زنگدار" else "Traditional resonant dual chime",
        SoundPlayer.SOUND_DIGITAL to if (language == AppLanguage.FA) "بوق‌های سریع الکترونیکی مچ‌بند دیجیتال" else "Crisp double-burst electronic alert",
        SoundPlayer.SOUND_LOUD to if (language == AppLanguage.FA) "صدای قوی، چندفرکانسه برای خواب‌های سنگین" else "High-energy multi-frequency alert",
        SoundPlayer.SOUND_SOFT_BELL to if (language == AppLanguage.FA) "ناقوس دلنشین با طنین و پژواک طبیعی" else "Acoustic temple bell with rich resonance"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = Strings.get(language, "sound_title"),
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Notice about system alarm volume
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == AppLanguage.FA)
                                "میزان بلندی صدای زنگ هشدار از بخش تنظیمات صدای سیستم (Alarm Volume) کنترل می‌شود."
                            else
                                "Alarm sound volume is controlled via your Android device's Alarm Volume slider in System Settings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sound Options List
            items(SoundPlayer.soundOptions) { snd ->
                val isSelected = snd == currentSound
                val isPlayingThis = playingSoundName == snd

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sound_item_$snd")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onSelectSound(snd)
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectSound(snd) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (snd) {
                                    SoundPlayer.SOUND_GENTLE -> Strings.get(language, "sound_gentle")
                                    SoundPlayer.SOUND_CLASSIC -> Strings.get(language, "sound_classic")
                                    SoundPlayer.SOUND_DIGITAL -> Strings.get(language, "sound_digital")
                                    SoundPlayer.SOUND_LOUD -> Strings.get(language, "sound_loud")
                                    SoundPlayer.SOUND_SOFT_BELL -> Strings.get(language, "sound_soft_bell")
                                    else -> snd
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            soundDescriptions[snd]?.let { desc ->
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Preview / Stop button
                        IconButton(
                            onClick = {
                                if (isPlayingThis) {
                                    onStopPreview()
                                } else {
                                    onPlayPreview(snd)
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isPlayingThis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (isPlayingThis) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingThis) "Stop" else "Preview",
                                tint = if (isPlayingThis) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
