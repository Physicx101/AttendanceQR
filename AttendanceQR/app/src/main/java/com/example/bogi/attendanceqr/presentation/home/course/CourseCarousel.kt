package com.example.bogi.attendanceqr.presentation.home.course

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bogi.attendanceqr.data.model.Course

class CourseCarousel(ctx: Context, attrs: AttributeSet): ConstraintLayout(ctx, attrs) {

    private var courseRecycler: CourseRecycler
    private var indicator: CourseIndicator

    init {
        LayoutInflater.from(context).inflate(com.example.bogi.attendanceqr.R.layout.course_carousel, this, true)
        indicator = findViewById(com.example.bogi.attendanceqr.R.id.indicator_course)
        courseRecycler = findViewById(com.example.bogi.attendanceqr.R.id.course_recycler)
    }

    fun init(data: List<Course>) {
        courseRecycler.init(data) {
            indicator.selected = it.first
            indicator.size = it.second
            indicator.invalidate()
        }

    }
}