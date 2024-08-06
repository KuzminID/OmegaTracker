package com.example.omegatracker.data

import kotlin.math.abs
import kotlin.time.Duration

fun Duration.componentsToString(
    hoursFormat: Char? = null,
    minutesFormat: Char? = null,
    secondsFormat: Char? = null
): String {
    return toComponents { hours, minutes, seconds, _ ->
        // Форматируем часы
        val formattedHours = if (seconds < 0) {
            "-${String.format("%02d", abs(hours))}"
        } else {
            String.format("%02d", hours)
        }

        // Форматируем минуты и секунды
        val formattedMinutes = String.format("%02d", abs(minutes))
        val formattedSeconds = String.format("%02d", abs(seconds))

        // Возвращаем строку с отформатированными часами, минутами и секундами
        "$formattedHours${hoursFormat ?: ""}:$formattedMinutes${minutesFormat ?: ""}:$formattedSeconds${secondsFormat ?: ""}"
    }
}

fun Duration.componentsToString(
    hoursFormat: Char,
    minutesFormat: Char
): String {
    return toComponents { hours, minutes, _, _ ->
        // Форматируем часы
        val formattedHours = if (hours < 0) {
            "-${String.format("%02d", abs(hours))}"
        } else {
            String.format("%02d", hours)
        }

        // Форматируем минуты
        val formattedMinutes = String.format("%02d", minutes)

        // Возвращаем строку с отформатированными часами и минутами
        "$formattedHours$hoursFormat:$formattedMinutes$minutesFormat"
    }
}