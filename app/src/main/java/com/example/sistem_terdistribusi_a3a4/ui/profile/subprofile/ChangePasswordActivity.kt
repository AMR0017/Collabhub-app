package com.example.sistem_terdistribusi_a3a4.ui.profile.subprofile

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityChangePasswordBinding
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

        auth = FirebaseAuth.getInstance()

        binding.buttonChangePass.setOnClickListener {
            binding.loading.visibility = ProgressBar.VISIBLE
            changePass()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // This ensures the back button works correctly
        return true
    }

    private fun changePass(){
        val newPass = binding.NewPasswordEdt.text.toString()
        val oldPass = binding.OldPasswordEdt.text.toString()
        val user: FirebaseUser? = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", oldPass)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    user.updatePassword(newPass)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful){
                                Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                                binding.NewPasswordEdt.text?.clear()
                                binding.OldPasswordEdt.text?.clear()
                                binding.loading.visibility = ProgressBar.GONE
                            }
                            else{
                                Toast.makeText(this, "Failed to changed password, Try again later", Toast.LENGTH_SHORT).show()
                                binding.loading.visibility = ProgressBar.GONE
                            }
                        }

                }
                else{
                    Toast.makeText(this, "Error, Check your old password credential", Toast.LENGTH_SHORT).show()
                    binding.loading.visibility = ProgressBar.GONE
                }
            }

    }

}