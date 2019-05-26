package com.example.bogi.attendanceqr.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.firebase.FireAuth
import com.example.bogi.attendanceqr.data.model.Course
import com.example.bogi.attendanceqr.presentation.MainActivity
import com.example.bogi.attendanceqr.presentation.common.BaseFragment
import com.example.bogi.attendanceqr.presentation.common.GlideApp
import com.example.bogi.attendanceqr.presentation.home.course.CourseListAdapter
import com.example.bogi.attendanceqr.presentation.login.AuthViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    val user = FireAuth.instance.getCurrentUser()!!
    var userNIU: String = ""

    val courseAdapter = CourseListAdapter(openCourseDetails())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.courses.observe(this, Observer {
            courseAdapter.submitList(it)
        })
        homeViewModel.getState().observe(this, Observer {
            userNIU = it.userNIU
            text_phone_user.text = it.userPhone
            text_NIM_user.text = userNIU
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_course.adapter = courseAdapter
        recycler_course.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        homeViewModel.getAllCourse()
        homeViewModel.getUserData(user.uid)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }*/
        //activity!!.window.statusBarColor = ContextCompat.getColor(view.context, R.color.white)
        text_name_user.text = user.displayName
        text_email_user.text = user.email
        GlideApp.with(this)
            .load(user.photoUrl)
            .fitCenter()
            .into(image_user)
        bt_logout.setOnClickListener {
            FireAuth.instance.checkOut(activity!!)
            (activity as MainActivity).userNotLogin()
        }
    }


    private fun openCourseDetails(): (Course) -> Unit = {
        homeViewModel.getState().value = homeViewModel.getState().value!!.copy(userName = user.displayName!!)
        homeViewModel.getState().value = homeViewModel.getState().value!!.copy(course = it)
        Log.d("COURSE", it.toString())
        (activity as MainActivity).openScanner()
    }


}