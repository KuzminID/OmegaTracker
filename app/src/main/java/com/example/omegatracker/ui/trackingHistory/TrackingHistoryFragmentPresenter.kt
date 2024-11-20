package com.example.omegatracker.ui.trackingHistory

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TrackingHistoryFragmentPresenter : BasePresenter<TrackingHistoryFragmentView>() {
    private val repository = appComponent.getTrackingHistoryRepository()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTrackingHistory() {
        launch(Dispatchers.IO) {
            val data = repository.getAllHistory()
            launch(Dispatchers.Main) {
                viewState.setAdapterData(filterChangesList(data))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeToHMS(time: Long?): String {
        launch(Dispatchers.Main) { }
        val dateTime = LocalDateTime.ofEpochSecond(
            time!! / 1000,
            (time % 1000).toInt(),
            java.time.ZoneOffset.UTC
        ) // Преобразуем в объект LocalDateTime
        val formatter = DateTimeFormatter.ofPattern("HHч:mmм:ssс") // Создаем форматтер для времени
        return dateTime.format(formatter) // Форматируем время
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterChangesList(trackingHistory: List<IssueAndHistory>): List<IssueAndHistory> {
        var history: List<IssueAndHistory> = listOf()
        history = trackingHistory.sortedByDescending {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            LocalDate.parse(it.history.historyGroup, formatter)
        }
//        val history: List<IssuesTrackingHistory> = trackingHistory.flatMap { it.history }
//            .sortedByDescending {
//                // Парсим строку даты в объект LocalDate
//                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
//                LocalDate.parse(it.historyGroup, formatter)
//            }
//        val issues: List<IssueEntity> = trackingHistory.map { it.issue }

        return history
    }
}