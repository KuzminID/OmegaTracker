package com.example.omegatracker.data.Repositories

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.repositories.TrackingHistoryRepository
import com.example.omegatracker.room.IssuesChangeList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackingHistoryRepositoryImpl :
    TrackingHistoryRepository {

    private val dao = appComponent.getChangesDao()
    override suspend fun getChangeList(): List<IssuesChangeList> {
        return dao.getChangesList()
    }

    override suspend fun insertChange(changeList: IssuesChangeList) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertData(changeList)
        }
    }

    override suspend fun clearDB() {
        dao.clearDB()
    }
}