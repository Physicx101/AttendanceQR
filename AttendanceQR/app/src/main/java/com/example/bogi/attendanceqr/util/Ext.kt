package com.example.bogi.attendanceqr.util

import android.content.res.Resources
import com.google.common.reflect.Reflection.getPackageName
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.toLocalTime(format: String): String {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat(format)
    sdf.timeZone = cal.timeZone
    return sdf.format(Date(this*1000))
}
fun Int.toIDR(): String = "IDR ${this.toPrice()}"
fun Int.toPrice(): String {
    val formatter = NumberFormat.getInstance() as DecimalFormat
    val symbols = formatter.decimalFormatSymbols
    symbols.groupingSeparator = '.'
    return formatter.format(this)
}
fun String.resValue(packageName: String, r: Resources): String {
    val resId = r.getIdentifier(this, "string", packageName)
    return r.getString(resId)
}
fun autoID(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val sb = StringBuilder()
    for (i in 0 until 20) sb.append(chars.random())
    return sb.toString()
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}