package com.example.omegatracker.entity

enum class NotificationActions(actionName : String, title : String) {

    StartAction("START","Start"),
    StopAction("STOP","Stop"),
    PauseAction("PAUSE","Pause"),
    StopAllAction("STOP_ALL","StopAll"),
    PauseAllAction("PAUSE_ALL","PauseAll");
}