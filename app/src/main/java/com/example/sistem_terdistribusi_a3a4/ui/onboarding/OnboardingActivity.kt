package com.example.sistem_terdistribusi_a3a4.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sistem_terdistribusi_a3a4.MainActivity
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityOnboardingBinding
import com.example.sistem_terdistribusi_a3a4.ui.homepage.HomepageActivity
import com.example.sistem_terdistribusi_a3a4.ui.login.LoginActivity
import com.example.sistem_terdistribusi_a3a4.ui.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this@OnboardingActivity, SignUpActivity::class.java)
            startActivity(intent)
        }


    }
}