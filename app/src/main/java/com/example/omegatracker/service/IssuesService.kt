package com.example.omegatracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.R
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.entity.Issue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

interface IssuesServiceBinder {
    fun startIssue(issue: Issue)
    fun stopIssue(issue: Issue)
    fun pauseIssue(issue: Issue)
    fun getResults(issue: Issue): Flow<Issue>
    fun stopRunningIssues()
}

@Singleton
class IssuesService : Service() {

    private val serviceBinder = object : IssuesServiceBinder, Binder() {

        override fun startIssue(issue: Issue) {
            taskManager.addIssue(issue)
            createNotificationForIssue(issue)
        }

        override fun stopIssue(issue: Issue) {
            taskManager.stopIssue(issue)
        }

        override fun pauseIssue(issue: Issue) {
            taskManager.stopIssue(issue)
        }

        override fun getResults(issue: Issue): Flow<Issue> {
            val flow = taskManager.getIssuesUpdates(issue)
            collectIssueUpdates(flow)
            return flow
        }

        override fun stopRunningIssues() {
            taskManager.stopRunningIssues()
        }
    }

    private lateinit var notificationManager: NotificationManager
    private val notificationBuilderList: MutableMap<Int, NotificationCompat.Builder> =
        mutableMapOf()

    private val taskManager = appComponent.getTaskManager()

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                vibrationPattern = LongArray(0) { 0 }
                enableVibration(true)
                enableLights(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationForIssue(issue: Issue) {
        val id = issue.id.hashCode()
        val builder = NotificationCompat.Builder(appComponent.getContext(), CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
            .setVibrate(LongArray(2) { 0 })
            .setContentTitle(issue.summary)
            .setContentText(
                "Remaining Time : ${
                    (issue.estimatedTime - issue.spentTime).componentsToString(
                        'ч',
                        'м',
                        'с'
                    )
                }"
            )
            .setSmallIcon(R.drawable.ic_launcher_omega_tracker_round) //TODO replace icon
        notificationBuilderList[id] = builder
        notificationManager.notify(id, builder.build())
        // startForeground(issueEntity.id.hashCode(),notification)
    }

    @OptIn(FlowPreview::class)
    fun collectIssueUpdates(flow: Flow<Issue>) {
        val updateDelay = 60000.milliseconds
        serviceScope.launch {
            flow.sample(updateDelay).collect {
                updateNotifications(it)
            }
        }
    }

    private fun updateNotifications(updatedIssue: Issue) {
        val id = updatedIssue.id.hashCode()
        val notificationBuilder = notificationBuilderList[id]
        serviceScope.launch {
            notificationBuilder
                ?.setContentText(
                    "Remaining Time : ${
                        (updatedIssue.estimatedTime - updatedIssue.spentTime).componentsToString(
                            'ч',
                            'м',
                            'с'
                        )
                    }"
                )
            notificationManager.notify(
                id, notificationBuilder?.build()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "issue_notification_channel"
        const val CHANNEL_NAME = "Issue Notifications"
    }
}