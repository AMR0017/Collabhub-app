package com.example.sistem_terdistribusi_a3a4.utils.other

import android.text.TextUtils
import java.util.regex.Pattern

fun String.isEmailValid(): Boolean  {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean{
    val passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$")
    return !TextUtils.isEmpty(this) && passwordPattern.matcher(this).matches()
}