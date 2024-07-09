package com.example.omegatracker.entity

sealed class Screens {
    data object StartScreens : Screens()
    data object AuthenticationScreens : Screens()
    data object IssuesScreens : Screens()
}