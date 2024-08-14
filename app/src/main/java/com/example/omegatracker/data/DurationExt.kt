package com.example.omegatracker.data

import kotlin.math.abs
import kotlin.time.Duration

fun Duration.componentsToString(
    hoursFormat: Char? = null,
    minutesFormat: Char? = null,
    secondsFormat: Char? = null
): String {
    return toComponents { hours, minutes, seconds, _ ->
        // hours formatting
        val formattedHours = if (seconds < 0 || minutes < 0) {
            "-${String.format("%02d", abs(hours))}"
        } else {
            String.format("%02d", hours)
        }

        // minutes and seconds formatting
        val formattedMinutes = String.format("%02d", abs(minutes))
        val formattedSeconds = String.format("%02d", abs(seconds))

        // returning string that contains formatted time including hours, minutes and seconds
        "$formattedHours${hoursFormat ?: ""}:$formattedMinutes${minutesFormat ?: ""}:$formattedSeconds${secondsFormat ?: ""}"
    }
}

fun Duration.componentsToString(
    hoursFormat: Char,
    minutesFormat: Char
): String {
    return toComponents { hours, minutes, _, _ ->
        val formattedHours = if (hours < 0) {
            "-${String.format("%02d", abs(hours))}"
        } else {
            String.format("%02d", hours)
        }
        val formattedMinutes = String.format("%02d", minutes)

        // returning string that contains formatted time including hours and minutes
        "$formattedHours$hoursFormat:$formattedMinutes$minutesFormat"
    }
}