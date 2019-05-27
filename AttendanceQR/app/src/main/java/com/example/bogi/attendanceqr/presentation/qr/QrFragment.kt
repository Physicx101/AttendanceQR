package com.example.bogi.attendanceqr.presentation.qr

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.model.Absen
import com.example.bogi.attendanceqr.data.model.Course
import com.example.bogi.attendanceqr.presentation.MainActivity
import com.example.bogi.attendanceqr.presentation.home.HomeViewModel
import com.example.bogi.attendanceqr.util.getCurrentDateTime
import com.example.bogi.attendanceqr.util.toString
import com.google.zxing.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_order_loading.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QrFragment : Fragment(), ZXingScannerView.ResultHandler {

    companion object {
        val instance = QrFragment()
    }

    private val homeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }
    val loadingDialog by lazy {
        val dialog = AlertDialog.Builder(activity!!)
            .setView(activity!!.layoutInflater.inflate(R.layout.dialog_order_loading, null)).create()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog
    }

    var userName: String = ""
    var userNIU: String = ""
    var courseId: String = ""

    private lateinit var scanner: ZXingScannerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getState().observe(this, Observer {
            userName = it.userName
            userNIU = it.userNIU
            courseId = it.course!!.id
            Log.d("ABSENQR", userNIU)
            Log.d("ABSENQR", userName)
            Log.d("ABSENQR", courseId)
        })
        activity!!.window.statusBarColor = ContextCompat.getColor(view.context, R.color.black)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            activity!!.window.decorView.systemUiVisibility = flags
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        scanner = ZXingScannerView(activity!!)
        return scanner
    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    @SuppressLint("CheckResult")
    override fun handleResult(rawResult: Result?) {
        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

        if (rawResult != null) {
            loadingDialog.show()
            loadingDialog.label_dialog_loading.text = "Loading ..."
            rawResult.let { result ->
                Log.d("ABSENQR", result.toString())
                if (!result.text.startsWith("simaster/")) wrongQRCode()
                else {
                    val data = result.text.split("/")
                    if (data.size != 2 || data[1] != courseId) wrongQRCode()
                    else {
                        val absen = Absen(courseId, userName, userNIU, dateInString, "")
                        homeViewModel.getState().value = homeViewModel.getState().value!!.copy(initialLoading = true)
                        Log.d("ABSEN", absen.toString())
                        homeViewModel.sendAbsen(absen)
                        loadingDialog.dismiss()
                        homeViewModel.getState().value = homeViewModel.getState().value!!.copy(initialLoading = false)
                        Toast.makeText(activity!!, "Absensi anda sudah masuk", Toast.LENGTH_LONG).show()
                        (activity as MainActivity).userHasCheckIn()
                    }
                }
            }
        } else scanner.resumeCameraPreview(this)
    }

    private fun wrongQRCode() {
        loadingDialog.dismiss()
        Toast.makeText(activity!!, "QR Code Salah", Toast.LENGTH_LONG).show()
        scanner.resumeCameraPreview(this)
    }

}