package com.example.bogi.attendanceqr.presentation.home.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.model.Course
import kotlinx.android.synthetic.main.item_course.view.*
import java.text.SimpleDateFormat
import java.util.*


class CourseListDosenAdapter(val onClick: (Course) -> Unit) :
    ListAdapter<Course, CourseListDosenAdapter.ViewHolder>(TaskDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var calendar = Calendar.getInstance()
        private var dayFormat = SimpleDateFormat("EEEE", Locale.US)
        private var day = dayFormat.format(calendar.time)

        fun bind(data: Course, pos: Int) = with(itemView) {
            bt_absen.visibility = View.INVISIBLE
            text_course.text = data.name
            text_lecturer.text = data.lecturer
            text_time.text = data.time
            text_room.text = data.room
            course_layout.setOnClickListener { onClick(data) }
            //bt_absen.text = "Mulai Kelas"
            //bt_absen.setOnClickListener { onClick(data) }
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}