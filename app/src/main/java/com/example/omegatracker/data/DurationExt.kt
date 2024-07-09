package com.example.omegatracker.data

import kotlin.time.Duration

fun Duration.componentsToString(
    hoursFormat: Char,
    minutesFormat: Char,
    secondsFormat: Char
): String {
    val hours = inWholeHours.toInt()
    val minutes = inWholeMinutes.toInt() % 60
    val seconds = inWholeSeconds.toInt() % 60
    return "$hours" +
            "$hoursFormat" +
            ":$minutes" +
            "$minutesFormat" +
            ":$seconds" +
            "$secondsFormat"
}

fun Duration.componentsToString(
    hoursFormat: Char,
    minutesFormat: Char
): String {
    val hours = inWholeHours.toInt()
    val minutes = inWholeMinutes.toInt() % 60
    return "$hours" +
            "$hoursFormat" +
            ":$minutes" +
            "$minutesFormat"
}