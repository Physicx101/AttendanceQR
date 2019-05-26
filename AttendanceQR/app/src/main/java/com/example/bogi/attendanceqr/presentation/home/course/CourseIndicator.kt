package com.example.bogi.attendanceqr.presentation.home.course

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CourseIndicator(ctx: Context, attrs: AttributeSet): View(ctx, attrs) {

    var selected = 0
    var size = 0
    val circle = Paint()
    val selectedCircle = Paint()
    val circleRadius = 10
    val circlePadding = 15

    init {
        circle.color = ContextCompat.getColor(context, com.example.bogi.attendanceqr.R.color.lightGray)
        circle.isAntiAlias = true
        selectedCircle.color = ContextCompat.getColor(context, com.example.bogi.attendanceqr.R.color.colorPrimary)
        selectedCircle.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val h: Int
        val desiredH = paddingTop + paddingBottom + circleRadius*2
        h = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredH, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredH
            else -> desiredH
        }
        setMeasuredDimension(widthSize, h)
    }

    override fun onDraw(canvas: Canvas) {
        if(size == 0) return
        var x = paddingStart + circleRadius
        val y = (height/2)
        for(i in 0 until size) {
            canvas.drawCircle(x.toFloat(), y.toFloat(), circleRadius.toFloat(), if(i == selected) selectedCircle else circle)
            x += circleRadius*2 + circlePadding
        }
    }

}