package com.example.omegatracker

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.room.IssueEntity
import com.example.omegatracker.room.IssuesDao
import com.example.omegatracker.room.IssuesDatabase
import com.example.omegatracker.room.IssuesTrackingHistory
import com.example.omegatracker.room.TrackingHistoryDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

//  Tests class
@RunWith(AndroidJUnit4::class)
class IssuesDaoTest {

    private lateinit var db: IssuesDatabase
    private lateinit var historyDao: TrackingHistoryDao
    private lateinit var issuesDao : IssuesDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, IssuesDatabase::class.java).build()
        historyDao = db.getTrackingHistoryDAO()
        issuesDao = db.getIssuesDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertHistory_shouldInsertIssue() =
        runBlocking {
            val issue = Issue(
                "0-1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
                "Project 1", "Project Name 1", true, IssueState.Open,created = 0, startTime = System.currentTimeMillis()
            )
            val history = IssuesTrackingHistory(
                historyElementID = 1,
                durationTime = 0,
                issueId = issue.id,
                endTime = System.currentTimeMillis(),
                issueStartTime = issue.startTime,
            )
            issuesDao.insertIssue(IssueEntity(issue))
            historyDao.insertData(history)

            val insertedHistory = historyDao.getHistoryById("1")
            val groupedElement = historyDao.getAllHistory()
            val firstHistory = groupedElement.flatMap { it.history }[0]
            println(firstHistory)
            assertNotNull(insertedHistory)
            assertEquals(history,firstHistory)
        }
//
//    //  Inserts test
//    @Test
//    fun insertIssue_shouldInsertIssue() =
//        runBlocking {
//            val issue = Issue(
//                "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//                "Project 1", "Project Name 1", true, IssueState.Open, System.currentTimeMillis()
//            )
//            issuesDao.insertIssue(IssueEntity(issue))
//
//            val insertedIssue = issuesDao.getIssueById("1")
//
//            assertNotNull(insertedIssue)
//            assertEquals(IssueEntity(issue), insertedIssue)
//        }
//
//    //  UpdatesTest
//    @Test
//    fun updateIssue_shouldUpdateIssue() = runBlocking {
//        val issue = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", false, IssueState.OnStop, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue))
//
//        val updatedIssue = issue.copy(summary = "Updated Issue 1")
//        issuesDao.updateIssue(IssueEntity(updatedIssue))
//
//        val updatedIssueFromDb = issuesDao.getIssueById("1")
//        assertNotNull(updatedIssueFromDb)
//        assertEquals(IssueEntity(updatedIssue), updatedIssueFromDb)
//    }
//
//    //  Deletes test
//    @Test
//    fun deleteIssue_shouldDeleteIssue() = runBlocking {
//        val issue = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue))
//        issuesDao.deleteIssue(IssueEntity(issue))
//
//        val deletedIssue = issuesDao.getIssueById("1")
//        assertNull(deletedIssue)
//    }
//
//    //  GetById test
//    @Test
//    fun getIssueById_shouldReturnIssue() = runBlocking {
//        val issue = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue))
//
//        val retrievedIssue = issuesDao.getIssueById("1")
//        assertNotNull(retrievedIssue)
//        assertEquals(IssueEntity(issue), retrievedIssue)
//    }
//
//    @Test
//    fun getIssueById_shouldReturnNullForNonExistentIssue() = runBlocking {
//        val retrievedIssue = issuesDao.getIssueById("2")
//        assertNull(retrievedIssue)
//    }
//
//    //  GetAll test
//    @Test
//    fun getAllIssues_shouldReturnAllIssues() = runBlocking {
//        val issue1 = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", false, IssueState.OnPause, System.currentTimeMillis()
//        )
//        val issue2 = Issue(
//            "2", "Issue 2", "Description 2", 3000.milliseconds, 4000.milliseconds,
//            "Project 2", "Project Name 2", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue1))
//        issuesDao.insertIssue(IssueEntity(issue2))
//
//        val issues = issuesDao.getAllIssues()
//        assertNotNull(issues)
//        assertEquals(2, issues.size)
//        issues.forEach {
//            assertEquals(IssueEntity(issue1), it)
//        }
//    }
//
//    //  GetByProject test
//    @Test
//    fun getIssuesByProject_shouldReturnIssuesFromSpecificProject() = runBlocking {
//        val issue1 = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", false, IssueState.OnPause, System.currentTimeMillis()
//        )
//        val issue2 = Issue(
//            "2", "Issue 2", "Description 2", 3000.milliseconds, 4000.milliseconds,
//            "Project 2", "Project Name 2", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue1))
//        issuesDao.insertIssue(IssueEntity(issue2))
//
//        val issuesFromProject1 = issuesDao.getIssuesByProject("Project 1")
//        assertEquals(1, issuesFromProject1.size)
//        assertEquals("1", issuesFromProject1[0].id)
//    }
//
//    //  GetActives test
//    @Test
//    fun getActiveIssues_shouldReturnOnlyActiveIssues() = runBlocking {
//        val issue1 = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", false, IssueState.OnStop, System.currentTimeMillis()
//        )
//        val issue2 = Issue(
//            "2", "Issue 2", "Description 2", 3000.milliseconds, 4000.milliseconds,
//            "Project 2", "Project Name 2", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue1))
//        issuesDao.insertIssue(IssueEntity(issue2))
//
//        val activeIssues = issuesDao.getActiveIssues()
//        assertEquals(1, activeIssues.size)
//        assertEquals("1", activeIssues[0].id)
//    }
//
//    //  Deactivation test
//    @Test
//    fun deactivateIssue_shouldSetIsActiveToFalse() = runBlocking {
//        val issue = Issue(
//            "1", "Issue 1", "Description 1", 1000.milliseconds, 2000.milliseconds,
//            "Project 1", "Project Name 1", true, IssueState.Open, System.currentTimeMillis()
//        )
//        issuesDao.insertIssue(IssueEntity(issue))
//
//        issuesDao.deactivateIssue("1")
//
//        val deactivatedIssue = issuesDao.getIssueById("1")
//        assertNotNull(deactivatedIssue)
//        assertEquals(false, deactivatedIssue?.isActive)
//    }
}
