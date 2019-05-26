package com.nomnom.nomnomuser.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.nomnom.nomnomuser.presentation.MainActivity
import com.nomnom.nomnomuser.R
import com.nomnom.nomnomuser.data.firebase.FireAuth
import com.nomnom.nomnomuser.presentation.menu.MenuViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment(R.layout.fragment_login) {

    private val authViewModel by lazy {
        ViewModelProviders.of(activity!!)[AuthViewModel::class.java]
    }
    private val menuViewModel by lazy {
        ViewModelProviders.of(activity!!)[MenuViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel.getCurrentUser().observe(this, Observer {
            it?.let {
                menuViewModel.getCheckIn()
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