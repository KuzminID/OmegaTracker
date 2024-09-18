package com.example.omegatracker.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent

class NotificationReceiver : BroadcastReceiver() {
    val repository = appComponent.getUserRepositoryImpl()
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {

            }
        }
    }
}