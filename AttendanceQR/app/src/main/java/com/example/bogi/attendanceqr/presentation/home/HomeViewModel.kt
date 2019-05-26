package com.example.bogi.attendanceqr.presentation.home

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bogi.attendanceqr.data.firebase.FireAuth
import com.example.bogi.attendanceqr.data.firebase.FireCourse
import com.example.bogi.attendanceqr.data.model.Absen
import com.example.bogi.attendanceqr.data.model.CheckIn
import com.example.bogi.attendanceqr.data.model.Course
import com.example.bogi.attendanceqr.presentation.common.BaseViewModel
import com.example.bogi.attendanceqr.util.toLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel: BaseViewModel(), LifecycleObserver {

    val fireCourse = FireCourse.instance
    val auth = FireAuth.instance
    val courses = MutableLiveData<List<Course>>()
    private val state =  MutableLiveData<HomeState>()
    val reviewLoading = MutableLiveData<Boolean>()

    init {
        state.value = HomeState()
        courses.value = mutableListOf()
    }

    fun getState() = state

    private fun getContent() {
        state.value = state.value!!.copy(contentLoading = true)
        getCourses()
    }

    private fun getCourses() {
        fireCourse.getCourses()?.let {
            state.value = state.value!!.copy(courses = it.toLiveData())
        }
    }

    fun reset() {
        state.value = HomeState()
    }



    fun getUserData(userId: String) {
        getUserNIU(userId)
        getUserPhone(userId)
        getUserRole(userId)
    }

    fun sendAbsen(absen: Absen) {
        compositeDisposable.add(
            fireCourse.saveAbsen(absen)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    reviewLoading.value = false
                }, {
                    Log.e("NOM", it.message)
                    reviewLoading.value = false
                })
        )
    }

    private fun getUserRole(userId: String) {
        compositeDisposable.add(
            auth.getUserRole(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    state.value = state.value!!.copy(userRole = it)
                    state.value = state.value!!.copy(initialLoading = false)
                    Log.d("USER", state.value.toString())
                }, {
                    Log.e("NOM", it.message)
                })
        )
    }

    private fun getUserNIU(userId: String) {
        compositeDisposable.add(
            auth.getUserNIU(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    state.value = state.value!!.copy(userNIU = it)
                    state.value = state.value!!.copy(initialLoading = false)
                    Log.d("USER", state.value.toString())
                }, {
                    Log.e("NOM", it.message)
                })
        )
    }

    private fun getUserPhone(userId: String) {
        compositeDisposable.add(
            auth.getUserPhone(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    state.value = state.value!!.copy(userPhone = it)
                    state.value = state.value!!.copy(initialLoading = false)
                    Log.d("USER", state.value.toString())
                }, {
                    Log.e("NOM", it.message)
                })
        )
    }

    fun getAllCourse() {
        compositeDisposable.add(
            fireCourse.getAllCourse()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    courses.value = it
                }, {
                    Log.e("NOM", it.message)
                })
        )
    }

    fun getCheckIn() {
        if(auth.getCurrentUser() == null) return
        state.value = state.value!!.copy(initialLoading = true)
        compositeDisposable.add(
            fireCourse.getCheckIn(auth.getCurrentUser()!!.uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    state.value = state.value!!.copy(checkIn = it, initialLoading = false)
                    getContent()
                }, { e ->
                    if(e.message == "empty") state.value = state.value!!.copy(initialLoading = false)
                })
        )
    }

    fun checkIn(checkInModel: CheckIn): Completable {
        return Completable.create { e ->
            fireCourse.checkIn(checkInModel, auth.getCurrentUser()!!.uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    e.onComplete()
                    state.value = state.value!!.copy(checkIn = checkInModel, initialLoading = false)
                    getContent()
                }, {
                    e.onError(Throwable(it.message))
                    state.value = state.value!!.copy(initialLoading = false)
                })
        }
    }
}

data class HomeState(
    var checkIn: CheckIn? = null,
    var courseName: String = "",
    var courseLecturer: String = "",
    var courseTime: String = "",
    var courseRoom: String = "",
    var course: Course? = null,
    var userName: String ="",
    var userNIU: String ="",
    var userPhone: String = "",
    var userRole: String = "",
    var courses: LiveData<List<Course>>? = null,
    var initialLoading: Boolean = false,
    var contentLoading: Boolean = false
)