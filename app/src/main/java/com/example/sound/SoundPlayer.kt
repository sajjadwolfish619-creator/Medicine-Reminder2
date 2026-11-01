package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundPlayer {

    const val SOUND_GENTLE = "Gentle"
    const val SOUND_CLASSIC = "Classic"
    const val SOUND_DIGITAL = "Digital"
    const val SOUND_LOUD = "Loud"
    const val SOUND_SOFT_BELL = "Soft Bell"

    val soundOptions = listOf(
        SOUND_GENTLE,
        SOUND_CLASSIC,
        SOUND_DIGITAL,
        SOUND_LOUD,
        SOUND_SOFT_BELL
    )

    private var currentTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun isPlaying(): Boolean {
        return currentTrack != null && currentTrack?.playState == AudioTrack.PLAYSTATE_PLAYING
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            currentTrack?.let {
                if (it.state == AudioTrack.STATE_INITIALIZED) {
                    it.stop()
                    it.release()
                }
            }
        } catch (_: Exception) {}
        currentTrack = null
    }

    fun play(soundName: String, onFinished: (() -> Unit)? = null) {
        stop()

        playbackJob = scope.launch {
            try {
                val sampleRate = 44100
                val pcmData = generateSoundPcm(soundName, sampleRate)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()

                val bufferSize = pcmData.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                currentTrack = track
                track.write(pcmData, 0, pcmData.size)
                track.play()

                // Wait until played
                val durationMs = (pcmData.size * 1000L) / sampleRate
                kotlinx.coroutines.delay(durationMs + 100)

                stop()
                onFinished?.invoke()
            } catch (_: Exception) {
                stop()
                onFinished?.invoke()
            }
        }
    }

    private fun generateSoundPcm(soundName: String, sampleRate: Int): ShortArray {
        return when (soundName) {
            SOUND_GENTLE -> generateGentle(sampleRate)
            SOUND_CLASSIC -> generateClassic(sampleRate)
            SOUND_DIGITAL -> generateDigital(sampleRate)
            SOUND_LOUD -> generateLoud(sampleRate)
            SOUND_SOFT_BELL -> generateSoftBell(sampleRate)
            else -> generateGentle(sampleRate)
        }
    }

    private fun generateGentle(sampleRate: Int): ShortArray {
        val totalSeconds = 2.0
        val totalSamples = (sampleRate * totalSeconds).toInt()
        val data = ShortArray(totalSamples)

        val notes = listOf(523.25, 659.25, 783.99, 1046.5) // C5, E5, G5, C6
        val noteDur = (sampleRate * 0.45).toInt()

        for (i in 0 until totalSamples) {
            val noteIdx = (i / noteDur).coerceAtMost(notes.size - 1)
            val freq = notes[noteIdx]
            val tInNote = (i % noteDur).toDouble() / sampleRate
            val env = exp(-3.0 * tInNote) * (1.0 - exp(-20.0 * tInNote))

            val sample = sin(2.0 * PI * freq * i / sampleRate) * env * 22000.0
            data[i] = sample.toInt().coerceIn(-32767, 32767).toShort()
        }
        return data
    }

    private fun generateClassic(sampleRate: Int): ShortArray {
        val totalSeconds = 1.8
        val totalSamples = (sampleRate * totalSeconds).toInt()
        val data = ShortArray(totalSamples)

        val beepSamples = (sampleRate * 0.25).toInt()
        val pauseSamples = (sampleRate * 0.15).toInt()

        var currentSample = 0
        val freqs = listOf(880.0, 587.33, 880.0, 587.33)

        for (freq in freqs) {
            for (j in 0 until beepSamples) {
                if (currentSample >= totalSamples) break
                val t = j.toDouble() / sampleRate
                val env = (1.0 - exp(-30.0 * t)) * exp(-2.5 * t)
                val sample = sin(2.0 * PI * freq * currentSample / sampleRate) * env * 24000.0
                data[currentSample++] = sample.toInt().coerceIn(-32767, 32767).toShort()
            }
            currentSample += pauseSamples
        }
        return data
    }

    private fun generateDigital(sampleRate: Int): ShortArray {
        val totalSeconds = 1.6
        val totalSamples = (sampleRate * totalSeconds).toInt()
        val data = ShortArray(totalSamples)

        val burstCount = 4
        val pulseLen = (sampleRate * 0.08).toInt()
        val gapLen = (sampleRate * 0.08).toInt()
        val burstGap = (sampleRate * 0.25).toInt()

        var idx = 0
        for (b in 0 until 2) {
            for (p in 0 until burstCount) {
                for (i in 0 until pulseLen) {
                    if (idx >= totalSamples) break
                    val sample = sin(2.0 * PI * 1760.0 * idx / sampleRate) * 20000.0
                    data[idx++] = sample.toInt().coerceIn(-32767, 32767).toShort()
                }
                idx += gapLen
            }
            idx += burstGap
        }
        return data
    }

    private fun generateLoud(sampleRate: Int): ShortArray {
        val totalSeconds = 2.0
        val totalSamples = (sampleRate * totalSeconds).toInt()
        val data = ShortArray(totalSamples)

        val pulseSamples = (sampleRate * 0.35).toInt()
        val pauseSamples = (sampleRate * 0.15).toInt()

        var current = 0
        while (current < totalSamples) {
            for (i in 0 until pulseSamples) {
                if (current >= totalSamples) break
                val s1 = sin(2.0 * PI * 1046.5 * current / sampleRate)
                val s2 = sin(2.0 * PI * 1318.5 * current / sampleRate)
                val s3 = sin(2.0 * PI * 1567.98 * current / sampleRate)
                val combined = (s1 * 0.4 + s2 * 0.35 + s3 * 0.25) * 31000.0
                data[current++] = combined.toInt().coerceIn(-32767, 32767).toShort()
            }
            current += pauseSamples
        }
        return data
    }

    private fun generateSoftBell(sampleRate: Int): ShortArray {
        val totalSeconds = 2.4
        val totalSamples = (sampleRate * totalSeconds).toInt()
        val data = ShortArray(totalSamples)

        val fundamental = 880.0
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-2.2 * t) * (1.0 - exp(-50.0 * t))
            val harmonic1 = sin(2.0 * PI * fundamental * t)
            val harmonic2 = 0.5 * sin(2.0 * PI * (fundamental * 1.5) * t) * exp(-3.0 * t)
            val harmonic3 = 0.25 * sin(2.0 * PI * (fundamental * 2.0) * t) * exp(-4.0 * t)

            val sample = (harmonic1 + harmonic2 + harmonic3) * env * 25000.0
            data[i] = sample.toInt().coerceIn(-32767, 32767).toShort()
        }
        return data
    }
}
