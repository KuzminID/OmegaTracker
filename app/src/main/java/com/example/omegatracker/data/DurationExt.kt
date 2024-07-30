package com.example.omegatracker.data

import kotlin.math.abs
import kotlin.time.Duration

fun Duration.componentsToString(
    hoursFormat: Char? = null,
    minutesFormat: Char? = null,
    secondsFormat: Char? = null
): String {
    val hours = inWholeHours.toInt()
    val minutes = abs(inWholeMinutes.toInt() % 60)
    val seconds = abs(inWholeSeconds.toInt() % 60)
    return "$hours" +
            "${hoursFormat?:""}" +
            ":$minutes" +
            "${minutesFormat?:""}" +
            ":$seconds" +
            "${secondsFormat?:""}"
}

fun Duration.componentsToString(
    hoursFormat: Char,
    minutesFormat: Char
): String {
    val hours = inWholeHours.toInt()
    val minutes = abs(inWholeMinutes.toInt() % 60)
    return "$hours" +
            "$hoursFormat" +
            ":$minutes" +
            "$minutesFormat"
}