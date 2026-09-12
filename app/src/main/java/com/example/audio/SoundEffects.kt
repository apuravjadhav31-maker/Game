package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class SoundEffects(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private var vibrator: Vibrator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.w("SoundEffects", "Could not initialize ToneGenerator", e)
        }

        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.w("SoundEffects", "Could not initialize Vibrator", e)
        }
    }

    fun playBarcodeBeep(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
            } catch (_: Exception) {}
        }
        if (hapticsEnabled) {
            triggerVibration(40)
        }
    }

    fun playCashDing(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
            } catch (_: Exception) {}
        }
        if (hapticsEnabled) {
            triggerVibration(70)
        }
    }

    fun playCardKeyBeep(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 50)
            } catch (_: Exception) {}
        }
        if (hapticsEnabled) {
            triggerVibration(25)
        }
    }

    fun playSuccess(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
            } catch (_: Exception) {}
        }
        if (hapticsEnabled) {
            triggerVibration(100)
        }
    }

    fun playError(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
            } catch (_: Exception) {}
        }
        if (hapticsEnabled) {
            triggerVibration(150)
        }
    }

    private fun triggerVibration(milliseconds: Long) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(milliseconds)
                }
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
