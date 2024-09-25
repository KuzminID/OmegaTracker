package com.example.omegatracker.ui.activities.trackingHistory

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class TrackingHistoryPresenter : BasePresenter<TrackingHistoryView>() {
    val repository = appComponent.getChangeListRepository()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getChangesList() {
        launch(Dispatchers.IO) {
            val data = repository.getChangeList()
            viewState.setAdapterData(filterChangesList(data))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeToHMS(time: Long?): String {
        val dateTime = LocalDateTime.ofEpochSecond(
            time!! / 1000,
            (time % 1000).toInt(),
            java.time.ZoneOffset.UTC
        ) // Преобразуем в объект LocalDateTime
        val formatter = DateTimeFormatter.ofPattern("HHч:mmм:ssс") // Создаем форматтер для времени
        return dateTime.format(formatter) // Форматируем время
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterChangesList(changesList: List<IssuesChangeList>): Map<String, List<IssuesChangeList>> {
        return changesList.groupBy {
            val date = Date(it.startTime)
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            formatter.format(date)
        }
    }
}