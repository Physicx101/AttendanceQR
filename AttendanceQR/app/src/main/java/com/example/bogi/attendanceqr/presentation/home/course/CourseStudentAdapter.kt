package com.example.bogi.attendanceqr.presentation.home.course

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.model.Student
import kotlinx.android.synthetic.main.item_students.view.*

class CourseStudentAdapter() :
    ListAdapter<Student, CourseStudentAdapter.ViewHolder>(TaskDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_students, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: Student, pos: Int) = with(itemView) {
            if (data.time == "null") text_student_time.visibility = View.INVISIBLE  else View.VISIBLE
            text_student_name.text = data.name
            text_student_time.text = data.time
            if (data.status == "Masuk") {
                text_student_absence.text = data.status.substring(0, 1)
                text_student_absence.setTextColor(Color.WHITE)
                text_student_absence.background.setTint(Color.GREEN)
            } else {
                text_student_absence.text = data.status.substring(0, 1)
                text_student_absence.setTextColor(resources.getColor(R.color.colorAccent))
                text_student_absence.background.setTint(resources.getColor(R.color.colorAccent))
            }

        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}