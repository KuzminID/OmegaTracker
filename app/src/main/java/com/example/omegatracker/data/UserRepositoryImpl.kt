package com.example.omegatracker.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.UserRepository
import com.example.omegatracker.entity.Value
import com.example.omegatracker.room.IssueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UserRepositoryImpl : UserRepository {

    private val youTrackApiService = appComponent.getYouTrackApiService()
    private val userManager = appComponent.getUserManager()
    private val issuesDao = appComponent.getIssuesDao()
    private var appStartTime by Delegates.notNull<Long>()

    override suspend fun authenticate(token: String, url: String): User {
        OmegaTrackerApplication.setBaseUrl(url)
        val user = youTrackApiService.sendAuthorizationRequest(token)
        val avatarUrl = user.avatarUrl
        user.avatarUrl = url + avatarUrl
        return user
    }

    override fun convertStringDateToMilliseconds(date: String?): Long {
        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        val newDate = format.parse(date)
        return newDate?.time ?: 0L
    }

    override suspend fun getIssuesList(): Flow<List<Issue>> = flow {
        //Defining variables for current date in milliseconds and parsed issues from server
        var dateMillis : Long = 0L
        var parsedIssues : List<Issue> = emptyList()

        //Getting tasks from the database has not yet received a response to the request
        val dbIssues = getAllIssuesFromDB()
        if (dbIssues.isNotEmpty()) {
            emit(dbIssues)
        }

        //Getting tasks and current date from server
        try {
            val (issuesFromServer, date) = youTrackApiService.getIssuesRequest(userManager.getToken())
            dateMillis = convertStringDateToMilliseconds(date)
            parsedIssues = parseIssue(issuesFromServer)
        } catch (e : Exception) {
            Log.d("getIssuesList",e.toString())
            dateMillis = System.currentTimeMillis()
        }

        appStartTime=dateMillis
        //TODO определить, что делать если время не пришло (способы проверки)
        compareIssues(dbIssues, parsedIssues)

        var updatedData = getAllIssuesFromDB()
        for (i in 0..10) {
            kotlinx.coroutines.delay(1000)
            updatedData = getAllIssuesFromDB()
            if (updatedData.isNotEmpty()) {
                break
            }
        }
        if (updatedData.isNotEmpty()) {
            emit(updatedData)
        } else {
            emit(emptyList())
        }

    }.flowOn(Dispatchers.IO)

    override fun getHelperData(): List<HelperContent> {
        return HelperContent.entries
    }

    override suspend fun parseIssue(issue: List<IssueFromJson>): List<Issue> {
        var data: List<Issue> = issue.map {
            val spentTime =
                it.customFields.find { it.type == "PeriodIssueCustomField" && it.id == "150-1" }
                    .let { field ->
                        when (field?.value) {
                            is Value.Period -> {
                                field.value.minutes?.toDuration(DurationUnit.MINUTES)
                                    ?: Duration.ZERO
                            }
                            else -> {
                                Duration.ZERO
                            }
                        }
                    }
            val estimatedTime =
                it.customFields.find { it.type == "PeriodIssueCustomField" && it.id == "150-0" }
                    .let { field ->
                        when (field?.value) {
                            is Value.Period -> {
                                field.value.minutes?.toDuration(DurationUnit.MINUTES)
                                    ?: Duration.ZERO
                            }
                            else -> {
                                Duration.ZERO
                            }
                        }
                    }
            var isResolved: Boolean
            val state = it.customFields.find { it.type == "StateIssueCustomField" }
                .let { field ->
                    field?.value as Value.State
                    isResolved = field.value.isResolved
                    if (isResolved) {
                        IssueState.Finished
                    } else {
                        IssueState.Open
                    }
                }
            Issue(
                id = it.id,
                summary = it.summary,
                description = it.description,
                spentTime = spentTime,
                estimatedTime = estimatedTime,
                projectShortName = it.project.shortName,
                projectName = it.project.name,
                state = state,
                updateTime = it.updated
            )
        }
        data = data.filter { it.state.stateName != IssueState.Finished.stateName }
        return data
    }

    //TODO переделано сравнение
    override fun compareIssues(dbIssues: List<Issue>?, serverIssues: List<Issue>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (dbIssues.isNullOrEmpty() && serverIssues.isEmpty()) {
                return@launch
            } else if (dbIssues.isNullOrEmpty()) {
                serverIssues.forEach {
                    upsertIssueToDB(it)
                }
            } else if (serverIssues.isEmpty()) {
                dbIssues.forEach {
                    upsertIssueToDB(it)
                }
            } else {
                val updatedIssues : MutableList<Issue> = mutableListOf()
                serverIssues.forEach { serverIssue->
                    val currentIssue : Issue? = dbIssues.find { it.id == serverIssue.id }
                    if (currentIssue != null) {
                        updatedIssues.add(Issue(
                            id = serverIssue.id,
                            description = serverIssue.description,
                            estimatedTime = serverIssue.estimatedTime,
                            spentTime = currentIssue.spentTime,
                            projectName = serverIssue.projectName,
                            projectShortName = serverIssue.projectShortName,
                            state = currentIssue.state,
                            summary = serverIssue.summary,
                            //TODO убрать update time
                            updateTime = serverIssue.updateTime,
                            startTime = currentIssue.startTime,
                            isActive = currentIssue.isActive
                        ))
                    } else {
                        updatedIssues.add(serverIssue)
                    }
                }
            }
        }
//        CoroutineScope(Dispatchers.IO).launch {
//            if (!dbIssues.isNullOrEmpty()) {
//                val updatedIssues = mutableListOf<Issue>()
//                serverIssues.forEach { serverIssue ->
//                    val curIssue: Issue? = dbIssues.find { it.id == serverIssue.id }
//                    if (curIssue != null) {
//                        if (curIssue.updateTime > serverIssue.updateTime) {
//                            updatedIssues.add(curIssue)
//                        } else {
//                            updatedIssues.add(serverIssue)
//                        }
//                    }
//                }
//                updatedIssues.forEach {
//                    upsertIssueToDB(it)
//                }
//            } else {
//                serverIssues.forEach {
//                    upsertIssueToDB(it)
//                }
//            }
//        }
    }

    override suspend fun upsertIssueToDB(issue: Issue) {
        issuesDao.insertIssue(IssueEntity(issue))
    }

    override suspend fun getAllIssuesFromDB(): List<Issue> {
        val entitiesList = issuesDao.getAllIssues()
        val issuesList = mutableListOf<Issue>()
        entitiesList.forEach {
            val issue = convertFromEntityToIssue(it)
            if (issue != null) {
                issuesList.add(issue)
            }
        }
        return issuesList.toList()
    }

    override suspend fun getIssueByIDFromDB(id: String): Issue? {
        val entity = issuesDao.getIssueById(id)
        return if (entity != null) {
            convertFromEntityToIssue(entity)
        } else {
            null
        }
    }

    override suspend fun deleteIssueFromDB(issue: Issue) {
        issuesDao.deleteIssue(IssueEntity(issue))
    }

    override suspend fun activateIssue(issue: Issue) {
        issuesDao.updateIssue(IssueEntity(issue))
    }

    override suspend fun deactivateIssue(issue: Issue) {
        issuesDao.deactivateIssue(issue.id)
    }

    override suspend fun clearDB() {
        issuesDao.deleteAll()
    }

    override suspend fun deactivateAllIssues() {
        issuesDao.deactivateAllIssues()
    }

    override fun convertFromEntityToIssue(entity: IssueEntity?): Issue? {
        if (entity != null) {
            return Issue(
                entity.id,
                entity.summary,
                entity.description,
                entity.spentTime.toDuration(DurationUnit.MILLISECONDS),
                entity.estimatedTime.toDuration(DurationUnit.MILLISECONDS),
                entity.projectShortName,
                entity.projectName,
                entity.isActive,
                IssueState.entries.find {
                    it.stateName == entity.state
                } ?: IssueState.Open,
                entity.startTime,
                entity.updateTime
            )
        } else {
            return null
        }
    }

}