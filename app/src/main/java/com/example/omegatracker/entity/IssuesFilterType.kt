package com.example.omegatracker.entity

import com.example.omegatracker.R

enum class IssuesFilterType(
    val headerText : Int,
    val btnText : Int) {
    Today(R.string.issues_header_show_all,R.string.issues_btn_show_current),All(R.string.issues_header_show_current,R.string.issues_btn_show_all)
}