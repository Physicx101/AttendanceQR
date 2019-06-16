package com.example.bogi.attendanceqr.data.firebase

import android.util.Log
import com.example.bogi.attendanceqr.data.model.Absen
import com.example.bogi.attendanceqr.data.model.CheckIn
import com.example.bogi.attendanceqr.data.model.Course
import com.example.bogi.attendanceqr.util.observesSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class FireCourse {

    companion object {
        val instance = FireCourse()
    }

    val db = FirebaseFirestore.getInstance()
    val courseInfo = mutableMapOf<String, Course>()
    var checkInModel: CheckIn? = null
    var courseName: String = ""

    init {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
    }

    fun saveAbsen(absen: Absen): Completable {
        return Completable.create { e ->
            db.collection("course")
                .document(absen.courseId)
                .collection("mahasiswa")
                .document(FireAuth.instance.getCurrentUser()!!.uid)
                .update(
                    mapOf(
                        Pair("id", FireAuth.instance.getCurrentUser()!!.uid),
                        Pair("studentName", absen.userName),
                        Pair("studentNIU", absen.userNIU),
                        Pair("time", absen.time),
                        Pair("status", "Masuk")
                    )
                )
                .addOnSuccessListener { e.onComplete() }
                .addOnFailureListener { e.onError(Throwable(it.message)) }
        }
    }

    fun getCourseInfo(courseId: String): Completable {
        return Completable.create { e ->
            db.collection("course")
                .document(courseId)
                .get()
                .addOnSuccessListener {
                    val info = Course(
                        courseId,
                        it["name"]?.toString() ?: "",
                        it["lecturer"]?.toString() ?: "",
                        it["time"]?.toString() ?: "",
                        it["room"]?.toString() ?: "",
                        it["day"]?.toString() ?: ""
                    )
                    courseInfo[courseId] = info
                    e.onComplete()
                }
                .addOnFailureListener {
                    Log.e("NOM", it.message)
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun getCheckIn(userId: String): Single<CheckIn> {
        return Single.create { e ->
            db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val courseId = it["courseId"]
                    val studentId = it["tableId"]
                    val time = it["time"]
                    val checkout = it["checkout"]?.toString()
                    if (courseId == null || studentId == null) e.onError(Throwable("empty"))
                    else {
                        val checkIn = CheckIn(courseId.toString(), studentId.toString(), time.toString(), checkout)
                        e.onSuccess(checkIn)
                        checkInModel = checkIn
                    }
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun getCourseName(courseId: String): Single<String> {
        return Single.create { e ->
            db.collection("course")
                .document(courseId)
                .get()
                .addOnSuccessListener {
                    val name = it["name"].toString()
                    e.onSuccess(name)
                    courseName = name
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun checkIn(checkInModel: CheckIn, userId: String): Completable {
        return Completable.create { e ->
            db.collection("course")
                .document(checkInModel.courseId)
                .get()
                .addOnSuccessListener {
                    if (it["name"] == null) {
                        e.onError(Throwable("Course is not available"))
                    } else {
                        val checkIn = HashMap<String, Any>()
                        checkIn["courseId"] = checkInModel.courseId
                        checkIn["tableId"] = checkInModel.studentId
                        checkIn["time"] = checkInModel.time
                        db.collection("user")
                            .document(userId)
                            .set(checkIn)
                            .addOnSuccessListener {
                                e.onComplete()
                                this.checkInModel = checkInModel
                            }
                            .addOnFailureListener { e.onError(Throwable(it.message)) }
                    }
                }
                .addOnFailureListener { e.onError(Throwable(it.message)) }
        }
    }

    fun getCourse(): Single<List<Course>> {
        return Single.create { e ->
            if (checkInModel != null) {
                checkInModel?.let {
                    db.collection("course")
                        .get()
                        .addOnSuccessListener { result ->
                            e.onSuccess(result as List<Course>)
                        }
                        .addOnFailureListener { err ->
                            e.onError(Throwable(err.message))
                        }
                }
            }
        }
    }

    fun getAllCourse(): Single<List<Course>> {
        return Single.create { e ->
            db.collection("course")
                .get()
                .addOnSuccessListener {
                    val list = mutableListOf<Course>()
                    it.map {
                        list.add(
                            Course(
                                it["id"].toString(),
                                it["name"].toString(),
                                it["lecturer"].toString(),
                                it["time"].toString(),
                                it["room"].toString(),
                                it["day"].toString()
                            )
                        )
                    }
                    e.onSuccess(list)
                }
                .addOnFailureListener {
                    Log.e("NOM", it.message)
                    e.onError(Throwable(it.message))
                }
        }
    }

    fun getCourses(): Flowable<List<Course>>? {
        if (checkInModel != null) {
            checkInModel?.let {
                return Flowable.defer {
                    db.collection("course")
                        .observesSnapshot()
                        .map {
                            it.map {
                                Course(
                                    it.id,
                                    it["name"].toString(),
                                    it["lecturer"].toString(),
                                    it["time"].toString(),
                                    it["room"].toString(),
                                    it["day"].toString()
                                )
                            }
                        }
                        .toFlowable(BackpressureStrategy.DROP)
                }
            }
        }
        return null
    }
}