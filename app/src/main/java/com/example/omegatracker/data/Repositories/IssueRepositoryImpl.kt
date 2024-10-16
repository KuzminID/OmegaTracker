package com.example.omegatracker.data.Repositories

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.OmegaTrackerApplication.Companion.appStartTime
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.entity.Value
import com.example.omegatracker.entity.repositories.IssueRepository
import com.example.omegatracker.room.IssueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.Date
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class IssueRepositoryImpl : IssueRepository {

    private val youTrackApiService = OmegaTrackerApplication.appComponent.getYouTrackApiService()
    private val userManager = OmegaTrackerApplication.appComponent.getUserManager()
    private val issuesDao = OmegaTrackerApplication.appComponent.getIssuesDao()

    override suspend fun getIssuesList(): Flow<List<Issue>> = flow {
        //Defining variables for current date in milliseconds and parsed issues from server
        var parsedIssues: List<Issue> = emptyList()

        //Getting tasks from the database has not yet received a response to the request
        val dbIssues = getAllIssuesFromDB()
        if (dbIssues.isNotEmpty()) {
            emit(dbIssues)
        }

        val issuesFromServer = youTrackApiService.getIssuesRequest(userManager.getToken())
        parsedIssues = parseIssue(issuesFromServer)


        compareIssues(dbIssues, parsedIssues)

        var updatedData = getAllIssuesFromDB()
        for (i in 0..10) {
            delay(1000)
            updatedData = getAllIssuesFromDB()
            if (updatedData.isNotEmpty()) {
                break
            }
        }
        if (updatedData.isNotEmpty()) {
            emit(updatedData)
        } else {
            emit(emptyList())
            throw UnknownHostException()
        }

    }.flowOn(Dispatchers.IO)

    override fun getIssuesHeaderData(): List<IssuesFilterType> {
        return IssuesFilterType.entries
    }

    override suspend fun parseIssue(issue: List<IssueFromJson>): List<Issue> {
        var data: List<Issue> = issue.map { issueFromJson ->
            val spentTime =
                issueFromJson.customFields.find { it.type == "PeriodIssueCustomField" && it.id == "150-1" }
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
                issueFromJson.customFields.find { it.type == "PeriodIssueCustomField" && it.id == "150-0" }
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
            val state = issueFromJson.customFields.find { it.type == "StateIssueCustomField" }
                .let { field ->
                    field?.value as Value.State
                    isResolved = field.value.isResolved
                    if (isResolved) {
                        IssueState.Finished
                    } else {
                        IssueState.Open
                    }
                }
            val created = issueFromJson.created
            Issue(
                id = issueFromJson.id,
                summary = issueFromJson.summary,
                description = issueFromJson.description,
                spentTime = spentTime,
                estimatedTime = estimatedTime,
                projectShortName = issueFromJson.project.shortName,
                projectName = issueFromJson.project.name,
                state = state,
                created = created
            )
        }
        data = data.filter { it.state.stateName != IssueState.Finished.stateName }
        return data
    }

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
                val updatedIssues: MutableList<Issue> = mutableListOf()
                serverIssues.forEach { serverIssue ->
                    val currentIssue: Issue? = dbIssues.find { it.id == serverIssue.id }
                    if (currentIssue != null) {
                        updatedIssues.add(
                            Issue(
                                id = serverIssue.id,
                                description = serverIssue.description,
                                estimatedTime = serverIssue.estimatedTime,
                                spentTime = currentIssue.spentTime,
                                projectName = serverIssue.projectName,
                                projectShortName = serverIssue.projectShortName,
                                state = currentIssue.state,
                                summary = serverIssue.summary,
                                startTime = currentIssue.startTime,
                                isActive = currentIssue.isActive,
                                created = serverIssue.created,
                            )
                        )
                    } else {
                        updatedIssues.add(serverIssue)
                    }
                }
                updatedIssues.forEach {
                    upsertIssueToDB(it)
                }
            }
        }
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

    override fun checkIsTimeToday(time: Long): Boolean {
        val createdTime = Date(time)
        val currentTime = Date(appStartTime)
        return (createdTime.day == currentTime.day)
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
                entity.created,
            )
        } else {
            return null
        }
    }
}