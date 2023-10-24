package com.example.sistem_terdistribusi_a3a4.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistem_terdistribusi_a3a4.data.user.Users
import com.example.sistem_terdistribusi_a3a4.databinding.ActivitySignUpBinding
import com.example.sistem_terdistribusi_a3a4.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance().reference

        binding.buttonSignup.setOnClickListener {
            val iUsername = binding.usernameEdt.text.toString()
            val iEmail = binding.emailEdt.text.toString()
            val iPassword = binding.passwordEdt.text.toString()
            val iPassConfirm = binding.ConfirmPasswordEdt.text.toString()

            if (iUsername.isEmpty() || iEmail.isEmpty() || iPassword.isEmpty() || iPassConfirm.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (iPassword != iPassConfirm) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = ProgressBar.VISIBLE

            // First, check if the email already exists
            db.child("users").orderByChild("email").equalTo(iEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(this@SignUpActivity, "Email address is already registered with a different username.", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = ProgressBar.GONE
                        } else {
                            // Email is unique; check if the username already exists
                            db.child("users").orderByChild("username").equalTo(iUsername)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(usernameSnapshot: DataSnapshot) {
                                        if (usernameSnapshot.exists()) {
                                            Toast.makeText(this@SignUpActivity, "Username already in use. Choose a different one.", Toast.LENGTH_LONG).show()
                                            binding.progressBar.visibility = ProgressBar.GONE
                                        } else {
                                            // Both email and username are unique; proceed with user creation
                                            mAuth.createUserWithEmailAndPassword(iEmail, iPassword)
                                                .addOnCompleteListener(this@SignUpActivity) { task ->
                                                    binding.progressBar.visibility = ProgressBar.GONE
                                                    if (task.isSuccessful) {
                                                        val users : Users = Users(email = iEmail, uName = iUsername, uid = mAuth.currentUser!!.uid)

                                                        // Replace Firestore code with Realtime Database code
                                                        db.child("users").child(mAuth.currentUser!!.uid).setValue(users)
                                                        Toast.makeText(this@SignUpActivity, "Sign-Up successful. Back to Login page.", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        val errorMessage = task.exception?.message ?: "Sign-up failed. Please try again."

                                                        if (task.exception is FirebaseAuthUserCollisionException) {
                                                            // Handle the specific case where the email is already in use
                                                            Toast.makeText(this@SignUpActivity, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show()
                                                            binding.progressBar.visibility = ProgressBar.GONE
                                                        } else {
                                                            Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                                            binding.progressBar.visibility = ProgressBar.GONE
                                                        }
                                                    }
                                                }
                                        }
                                    }

                                    override fun onCancelled(usernameError: DatabaseError) {
                                        handleDatabaseError(usernameError)
                                        binding.progressBar.visibility = ProgressBar.GONE
                                    }
                                })
                        }
                    }

                    override fun onCancelled(emailError: DatabaseError) {
                        handleDatabaseError(emailError)
                        binding.progressBar.visibility = ProgressBar.GONE
                    }
                })
        }

        binding.backToLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }
    private fun handleDatabaseError(databaseError: DatabaseError){
        val errorMessage: String = when (databaseError.code) {
            DatabaseError.PERMISSION_DENIED -> {
                "Permission denied. You don't have access to this data."
            }
            DatabaseError.DISCONNECTED -> {
                "Disconnected from the database. Please check your internet connection."
            }
            else -> {
                "An error occurred: ${databaseError.message}"
            }
        }
        // Display an error message to the user (you can use Toast or a dialog)
        Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_LONG).show()

        // You can also log the error for debugging purposes
        Log.e("FirebaseDatabaseError", errorMessage)
    }
}