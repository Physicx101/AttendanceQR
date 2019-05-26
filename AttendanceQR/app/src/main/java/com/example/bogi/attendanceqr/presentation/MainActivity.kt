package com.example.bogi.attendanceqr.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.firebase.FireAuth
import com.example.bogi.attendanceqr.presentation.home.HomeFragment
import com.example.bogi.attendanceqr.presentation.home.HomeViewModel
import com.example.bogi.attendanceqr.presentation.login.LoginFragment
import com.example.bogi.attendanceqr.presentation.qr.QrFragment

class MainActivity : AppCompatActivity() {

    private val homeViewModel by lazy {
        ViewModelProviders.of(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initServiceLifecycle()
        if (FireAuth.instance.getCurrentUser() == null) userNotLogin() else {
            userHasCheckIn()
            //homeViewModel.getCheckIn()
        }
    }

    fun userNotLogin() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LoginFragment())
            .commit()
    }

    fun userHasCheckIn() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, HomeFragment())
            .commit()
    }

    fun openScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            getPermission()
            return
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, QrFragment.instance)
            .commit()
    }

    private fun initServiceLifecycle() {
        lifecycle.addObserver(homeViewModel)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner()
            } else {
                getPermission()
            }
        }
    }

    private fun getPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
    }
}
