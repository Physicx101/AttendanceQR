package com.example.bogi.attendanceqr.data.firebase

import android.util.Log
import com.example.bogi.attendanceqr.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import io.reactivex.Single

class FireStudents {

    companion object {
        val instance = FireStudents()
    }

    val db = FirebaseFirestore.getInstance()

    init {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
    }

    fun getCourseStudents(courseId: String): Single<List<Student>> {
        return Single.create { e ->
            Log.d("COURSEID", courseId)
            db.collection("course")
                .document(courseId)
                .collection("mahasiswa")
                .orderBy("studentName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    val list = mutableListOf<Student>()
                    it.map {
                        list.add(
                            Student(
                                it["id"].toString(),
                                it["studentName"].toString(),
                                it["status"].toString(),
                                it["time"].toString()
                            )
                        )
                    }
                    e.onSuccess(list)
                }
                .addOnFailureListener {
                    Log.e("STUDENTSERRFIRE", it.message)
                    e.onError(Throwable(it.message))
                }
        }
    }
}