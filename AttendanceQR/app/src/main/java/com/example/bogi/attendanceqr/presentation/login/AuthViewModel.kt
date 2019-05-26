package com.example.bogi.attendanceqr.presentation.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.bogi.attendanceqr.data.firebase.FireAuth
import com.example.bogi.attendanceqr.presentation.common.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthViewModel : BaseViewModel() {

    private val user = MutableLiveData<FirebaseUser?>()
    private val auth = FireAuth.instance
    private val signInLoading = MutableLiveData<Boolean>()
    private val state =  MutableLiveData<AuthState>()

    init {
        state.value = AuthState()
        user.value = auth.getCurrentUser()
        signInLoading.value = false
    }

    fun getCurrentUser() = user
    fun getSignInLoading() = signInLoading

    fun signInWithGoogle(account: GoogleSignInAccount) {
        signInLoading.value = true
        compositeDisposable.add(
            auth.signInWithGoogle(account)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    signInLoading.value = false
                    user.value = auth.getCurrentUser()
                }, { e ->
                    signInLoading.value = false
                    Log.e("NOM", e.message)
                })
        )
    }



}

data class AuthState(
    var userNIU: String = "",
    var userPhone: String? = ""
)