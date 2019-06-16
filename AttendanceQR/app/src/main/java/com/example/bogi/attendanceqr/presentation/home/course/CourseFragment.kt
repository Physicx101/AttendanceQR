package com.example.bogi.attendanceqr.presentation.home.course

import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.presentation.common.BaseFragment
import com.example.bogi.attendanceqr.presentation.home.HomeViewModel
import com.example.bogi.attendanceqr.presentation.qr.GenerateQrFragment
import kotlinx.android.synthetic.main.fragment_course.*

class CourseFragment: BaseFragment(R.layout.fragment_course) {

    private val courseViewModel by lazy {
        ViewModelProviders.of(this)[CourseViewModel::class.java]
    }

    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    var courseId: String = ""
    val courseStudentAdapter = CourseStudentAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.getState().observe(this, Observer {
            courseId = it.course!!.id
            courseViewModel.getStudents(courseId)
        })
        courseViewModel.students.observe(this, Observer {
            courseStudentAdapter.submitList(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_back.setOnClickListener { activity!!.onBackPressed() }
        btn_start_class.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.container, GenerateQrFragment())
                .addToBackStack(null)
                .commit()
        }
        val itemDecor = DividerItemDecoration(context, ClipDrawable.HORIZONTAL)
        recycler_course_students.addItemDecoration(itemDecor)
        recycler_course_students.adapter = courseStudentAdapter
        recycler_course_students.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }
}