package com.example.bogi.attendanceqr.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Observable
import org.reactivestreams.Publisher

fun Query.observesSnapshot() = observeQuerySnapshot(this)

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>

fun observeQuerySnapshot(ref: Query): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                emitter.tryOnError(error?.cause!!)
            } else {
                emitter.onNext(snapshot)
            }
        }
        emitter.setCancellable { listener.remove() }
    }
}