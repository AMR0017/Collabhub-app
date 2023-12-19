package com.example.sistem_terdistribusi_a3a4.utils.other

import android.icu.text.SimpleDateFormat
import android.text.TextUtils
import com.google.common.io.BaseEncoding
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean  {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean{
    val passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$")
    return !TextUtils.isEmpty(this) && passwordPattern.matcher(this).matches()
}

fun generateInvitationCode(): String{
    val uuid = UUID.randomUUID()
    val code = BaseEncoding.base64Url().omitPadding().encode(uuid.toString().toByteArray())
    return code.take(15)
}
fun calculateRemainingDays(dueDate: String?): Int {
    if (dueDate.isNullOrBlank()) {
        return -1 // Indicate an error or undefined due date
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    try {
        val currentDate = Calendar.getInstance()
        val dueDateCalendar = Calendar.getInstance()
        dueDateCalendar.time = sdf.parse(dueDate) ?: return -1

        // Calculate the difference in days
        val diffMillis = dueDateCalendar.timeInMillis - currentDate.timeInMillis
        val diffDays = diffMillis / (24 * 60 * 60 * 1000)

        return diffDays.toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        return -1 // Indicate an error in date parsing
    }
}