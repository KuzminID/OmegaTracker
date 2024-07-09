package com.example.omegatracker.entity

interface IssueI {
    val id: String
    val summary: String?
    val description: String?
    val projectShortName: String?
    val projectName: String?
    var isActive: Boolean
    val startTime: Long
}