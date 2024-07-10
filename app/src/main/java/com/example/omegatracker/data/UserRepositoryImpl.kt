package com.example.omegatracker.data

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
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UserRepositoryImpl : UserRepository {

    private val youTrackApiService = appComponent.getYouTrackApiService()
    private val userManager = appComponent.getUserManager()
    private val issuesDao = appComponent.getIssuesDao()

    override suspend fun authenticate(token: String, url: String): User {
        OmegaTrackerApplication.setBaseUrl(url)
        return youTrackApiService.sendAuthorizationRequest(token)
    }

    override suspend fun getIssuesList(): Flow<List<Issue>> = flow {
        //Getting tasks from the database has not yet received a response to the request
        val dbIssues = getAllIssuesFromDB()
        if (dbIssues.isNotEmpty()) {
            emit(dbIssues)
        }
        val issuesFromServer = youTrackApiService.getIssuesRequest(userManager.getToken())

        val parsedIssues = parseIssue(issuesFromServer)

        compareIssues(dbIssues, parsedIssues)

        var updatedData = getAllIssuesFromDB()
        while (updatedData.isEmpty()) {
            kotlinx.coroutines.delay(1000)
            updatedData = getAllIssuesFromDB()
        }
        emit(updatedData)

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
            var isResolved = false
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
                lastUpdatedTime = it.updated
            )
        }
        data = data.filter { it.state.stateName != IssueState.Finished.stateName }
        return data
    }

    //Comparing local issues from server issues using last update time
    override fun compareIssues(dbIssues: List<Issue>?, serverIssues: List<Issue>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!dbIssues.isNullOrEmpty()) {
                val updatedIssues = mutableListOf<Issue>()
                serverIssues.forEach { serverIssue ->
                    val curIssue: Issue? = dbIssues.find { it.id == serverIssue.id }
                    if (curIssue != null) {
                        if (curIssue.lastUpdatedTime > serverIssue.lastUpdatedTime) {
                            updatedIssues.add(curIssue)
                        } else {
                            updatedIssues.add(serverIssue)
                        }
                    }
                }
                updatedIssues.forEach {
                    upsertIssueToDB(it)
                }
            } else {
                serverIssues.forEach {
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
            if (issue!=null) {
                issuesList.add(issue)
            }
        }
        return issuesList.toList()
    }

    override suspend fun getIssueByIDFromDB(id: String): Issue? {
        val entity = issuesDao.getIssueById(id)
        return if (entity!=null) {
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

    override suspend fun deactivateAllTasks() {
        issuesDao.deactivateAllIssues()
    }

    override fun convertFromEntityToIssue(entity: IssueEntity?): Issue? {
        if (entity!=null) {
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