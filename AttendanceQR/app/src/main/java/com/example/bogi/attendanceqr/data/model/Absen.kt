package com.example.bogi.attendanceqr.data.model

data class Absen(
    val courseId: String,
    val userName: String,
    val userNIU: String,
    val time: String,
    val status: String,
    val checkout: String? = null
)