package com.example.bogi.attendanceqr.presentation.qr

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.model.Course
import com.example.bogi.attendanceqr.presentation.common.BaseFragment
import com.example.bogi.attendanceqr.presentation.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_qr.*
import net.glxn.qrgen.android.QRCode

class GenerateQrFragment : BaseFragment(R.layout.fragment_qr) {

    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.getState().value?.course.let { course ->
            setupUI(course!!)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.window.statusBarColor = ContextCompat.getColor(view.context, com.example.bogi.attendanceqr.R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            activity!!.window.decorView.systemUiVisibility = flags
        }
        bt_close_qr.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun setupUI(course: Course) {
        val qrText = "simaster/"
        text_course_name.text = course.name
        text_course_lecturer.text = course.lecturer
        text_course_time.text = course.time
        val dp = 250f
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
        img_qrcode.setImageBitmap(QRCode.from(qrText + course.id).withSize(px, px).bitmap())}
}