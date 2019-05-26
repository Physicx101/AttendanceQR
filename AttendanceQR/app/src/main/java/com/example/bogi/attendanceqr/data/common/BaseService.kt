package com.example.bogi.attendanceqr.data.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.firestore.ListenerRegistration

open class BaseService: LifecycleObserver {

    val listeners = mutableListOf<ListenerRegistration>()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        listeners.forEach { it.remove() }
    }

    fun clear() = onDestroy()

}