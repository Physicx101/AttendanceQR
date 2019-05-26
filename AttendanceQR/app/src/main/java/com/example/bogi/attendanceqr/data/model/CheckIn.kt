package com.example.bogi.attendanceqr.data.model

data class CheckIn(
    val courseId: String,
    val studentId: String,
    val time: String,
    val checkout: String? = null
)