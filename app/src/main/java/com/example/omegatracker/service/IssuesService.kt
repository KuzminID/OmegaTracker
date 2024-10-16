package com.example.omegatracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.R
import com.example.omegatracker.data.NotificationReceiver
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.ui.timer.IssueTimerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

interface IssuesServiceBinder : Serializable {
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
            cancelNotificationForIssue(issue)
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
            cancelAllNotifications()
        }
    }

    private lateinit var notificationManager: NotificationManager
    private var notificationBuilderList: MutableMap<Int, NotificationCompat.Builder> =
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

    private fun cancelNotificationForIssue(issue: Issue) {
        val id = issue.id.hashCode()
        Log.d("Cancel Notification", id.toString())
        notificationManager.cancel(id)
        notificationBuilderList.remove(id)
        if (notificationBuilderList.isEmpty()) {
            stopForeground(true)
        }
    }

    private fun cancelAllNotifications() {
        notificationManager.cancelAll()
        notificationBuilderList = mutableMapOf()
        stopForeground(true)
    }

    private fun createNotificationForIssue(issue: Issue) {
        createSummaryNotification()
        val id = issue.id.hashCode()
        val context = appComponent.getContext()
        Log.d("Create Notification", id.toString())
        val intent = Intent(this, IssueTimerActivity::class.java)
        intent.putExtra("issue_id", issue.id)
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(id, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
            .setVibrate(LongArray(2) { 0 })
            .setContentTitle(issue.summary)
            .setOngoing(true)
            .setGroup("issues_group")
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
            .setContentIntent(pendingIntent)

        when (issue.state) {
            IssueState.Finished -> Log.d("Notification creation", "This issue is finished")
            IssueState.Open -> Log.d("Notification creation", "This issue is not running")

            IssueState.OnStop -> Log.d("Notification creation", "This issue in stopped")
            IssueState.OnWork -> {
                builder.addAction(
                    R.drawable.pause_icon,
                    "Пауза",
                    PendingIntent.getBroadcast(
                        context, id, Intent(context, NotificationReceiver::class.java).apply {
                            action = "PAUSE"
                            putExtra("issue_id", id)
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                builder.addAction(
                    R.drawable.stop_icon,
                    "Стоп",
                    PendingIntent.getBroadcast(
                        context, id, Intent(context, NotificationReceiver::class.java).apply {
                            action = "STOP"
                            putExtra("issue_id", id)
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }

            IssueState.OnPause -> {
                builder.addAction(
                    R.drawable.play_icon,
                    "Старт",
                    PendingIntent.getBroadcast(
                        context, id, Intent(context, NotificationReceiver::class.java).apply {
                            action = "START"
                            putExtra("issue_id", id)
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                builder.addAction(
                    R.drawable.pause_icon,
                    "Стоп",
                    PendingIntent.getBroadcast(
                        context, id, Intent(context, NotificationReceiver::class.java).apply {
                            action = "STOP"
                            putExtra("issue_id", id)
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }
        }
        notificationBuilderList[id] = builder
        val notification = builder.build()
        notificationManager.notify(id, notification)
        startForeground(issue.id.hashCode(), notification)
    }

    private fun createSummaryNotification() {
        val context = appComponent.getContext()
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_omega_tracker_round) //TODO replace icon
            .setContentTitle("Задачи")
            .setContentText("Все запущенные задачи")
            .setGroup("issues_group") // Идентификатор группы уведомлений
            .setGroupSummary(true) // Обозначить как сводное уведомление
            .setAutoCancel(true)

        // Добавление кнопок
        notificationBuilder.addAction(
            R.drawable.pause_icon, //TODO replace icon
            "Пауза",
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationReceiver::class.java).apply {
                    action = "PAUSE_ALL"
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        notificationBuilder.addAction(
            R.drawable.stop_icon, //TODO replace icon
            "Стоп",
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationReceiver::class.java).apply {
                    action = "STOP_ALL"
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        // Отправка уведомления
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    @OptIn(FlowPreview::class)
    fun collectIssueUpdates(flow: Flow<Issue>) {
        val updateDelay = 1000.milliseconds
        serviceScope.launch {
            flow.sample(updateDelay).collect {
                updateNotifications(it)
            }
        }
    }

    private fun updateNotifications(updatedIssue: Issue) {
        val id = updatedIssue.id.hashCode()
        val notificationBuilder = notificationBuilderList[id]
        val notification = notificationBuilder?.build()
        if (notification != null) {
            serviceScope.launch {
                notificationBuilder
                    .setContentText(
                        "Remaining Time : ${
                            (updatedIssue.estimatedTime - updatedIssue.spentTime).componentsToString(
                                'ч',
                                'м',
                                'с'
                            )
                        }"
                    )
                notificationManager.notify(
                    id, notification
                )
            }
        } else {
            Log.d("Update Notification Fun", "notification was null")
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