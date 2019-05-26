package com.example.bogi.attendanceqr.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.firebase.FireAuth
import com.example.bogi.attendanceqr.presentation.common.BaseFragment
import com.example.bogi.attendanceqr.presentation.home.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment(R.layout.fragment_login) {

    private val authViewModel by lazy {
        ViewModelProviders.of(activity!!)[AuthViewModel::class.java]
    }
    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel.getCurrentUser().observe(this, Observer {
            it?.let {
                homeViewModel.getCheckIn()
                //activity!!.onBackPressed()
            }
        })
        authViewModel.getSignInLoading().observe(this, Observer {
            it?.let { loading ->
                loading_login.visibility = if(loading) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_login_google.setOnClickListener {
            authViewModel.getSignInLoading().value = true
            startActivityForResult(FireAuth.instance.googleSignInIntent(activity!!), FireAuth.GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FireAuth.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authViewModel.signInWithGoogle(account!!)
            } catch (e: ApiException) {
                authViewModel.getSignInLoading().value = false
            }
        }
    }

}