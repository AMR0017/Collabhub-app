package com.example.sistem_terdistribusi_a3a4.utils.edittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.sistem_terdistribusi_a3a4.utils.other.isPasswordValid
import com.google.android.material.textfield.TextInputEditText

class CustomPassword : TextInputEditText {
    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super (context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputan = s.toString()
                when {
                    inputan.length < 8 -> error = "Password Must Be At Least 8 Characters Length"
                    !inputan.isPasswordValid() -> error = "Password must contain at least 8 characters, including letters and numbers."
                }
            }

            override fun afterTextChanged(p0: Editable?) {


            }
        })
    }
}