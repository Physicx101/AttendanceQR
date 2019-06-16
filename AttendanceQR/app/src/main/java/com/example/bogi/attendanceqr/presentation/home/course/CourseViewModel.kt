package com.example.bogi.attendanceqr.presentation.home.course

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bogi.attendanceqr.data.firebase.FireStudents
import com.example.bogi.attendanceqr.data.model.Student
import com.example.bogi.attendanceqr.presentation.common.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CourseViewModel: BaseViewModel() {

    val studentDb = FireStudents.instance
    val students = MutableLiveData<List<Student>>()

    init {
        students.value = mutableListOf()
    }

    fun getStudents(courseId: String) {
        compositeDisposable.add(
            studentDb.getCourseStudents(courseId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("STUDENT", it.toString())
                    students.value = it
                }, {
                    Log.e("STUDENTSERR", it.message)
                })
        )
    }

}