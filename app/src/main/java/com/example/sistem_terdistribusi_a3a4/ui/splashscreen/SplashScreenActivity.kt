package com.example.sistem_terdistribusi_a3a4.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityOnboardingBinding
import com.example.sistem_terdistribusi_a3a4.ui.homepage.HomepageActivity
import com.example.sistem_terdistribusi_a3a4.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser!=null){
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}