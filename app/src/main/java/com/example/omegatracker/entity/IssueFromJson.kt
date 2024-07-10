package com.example.omegatracker.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class State(
    val appStateValue: String,
    val jsonStateValue: String
) {
    InProgress("В работе", "In Progress"),
    Open("Открыта", "Open"),
    Submitted("Создана", "Submitted"),
    Finished("Завершена", "Fixed")
}

@JsonClass(generateAdapter = true)
data class IssueFromJson(
    val id: String,
    val summary: String?,
    val description: String?,
    val project: Project,
    val customFields: List<CustomField>,
    val updated : Long
)

@JsonClass(generateAdapter = true)
data class Project(
    val name: String?,
    val shortName: String?
)

@JsonClass(generateAdapter = true)
data class CustomField(
    val name: String?,
    val value: Value?,
    @Json(name = "\$type") val type: String,
    val id: String
)

sealed class Value {
    data class State(
        val name: String,
        val isResolved: Boolean
    ) : Value()

    @JsonClass(generateAdapter = true)
    data class Period(
        val minutes: Int?
    ) : Value()

    data class DefaultValue(
        val name: String? = null
    ) : Value()
}

//@JsonClass(generateAdapter = true)
//sealed class CustomField (open val name : String?) {
//
//    @JsonClass(generateAdapter = true)
//    data class IssueCustomFieldWithValue (
//        override val name: String?,
//        val value: Value?
//    ) : CustomField(name)
//
//    @JsonClass(generateAdapter = true)
//    data class IssueCustomFieldArrayValue (
//        override val name : String?,
//        val value : Array<Value>
//    ) : CustomField(name)
//
//    @JsonClass(generateAdapter = true)
//    data class DefaultIssueCustomField (
//        override val name : String?,
//        val value : Value?
//    ) : CustomField(name)
//}
