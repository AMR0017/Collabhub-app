package com.example.sistem_terdistribusi_a3a4.ui.profile.subprofile

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityEditProfileBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

        val datePIcker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select your date of birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePIcker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            binding.DoBEdt.setText(sdf.format(it))
        })



        binding.genderAuto.isEnabled = false
        binding.usernameEdt.isClickable = false
        binding.usernameEdt.isFocusable = false
        binding.usernameEdt.isFocusableInTouchMode = false  // Set this to true
        binding.fullnameEdt.isClickable = false
        binding.fullnameEdt.isFocusable = false
        binding.fullnameEdt.isFocusableInTouchMode = false  // Set this to true
        binding.DoBEdt.isClickable = false
        binding.DoBEdt.isFocusable = false
        binding.buttonEditProfile.text = "Edit Profile"


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val currentUid = auth.currentUser?.uid

        if (currentUid != null){
            val userRef = databaseReference.child("users").child(currentUid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val username = snapshot.child("uname").value.toString()
                        val fName = snapshot.child("fname").value.toString()
                        val gender = snapshot.child("gender").value.toString()
                        val dbirth = snapshot.child("dbirth").value.toString()

                        binding.usernameEdt.setText(username)
                        binding.fullnameEdt.setText(fName)
                        binding.DoBEdt.setText(dbirth)
                        binding.genderAuto.setText(gender)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                }
            })
        }

        binding.buttonEditProfile.setOnClickListener {
            if (binding.buttonEditProfile.text == "Edit Profile"){
                binding.buttonEditProfile.text = "Save Profile"
                binding.usernameEdt.isClickable = true
                binding.usernameEdt.isFocusable = true
                binding.usernameEdt.isFocusableInTouchMode = true
                binding.fullnameEdt.isClickable = true
                binding.fullnameEdt.isFocusable = true
                binding.fullnameEdt.isFocusableInTouchMode = true
                binding.DoBEdt.isClickable = true
                binding.DoBEdt.isFocusable = true
                binding.genderAuto.isEnabled = true

                binding.DoBEdt.setOnClickListener {
                    datePIcker.show(supportFragmentManager, datePIcker.toString())
                }

                binding.DoBLayoutEdt.setEndIconOnClickListener{
                    datePIcker.show(supportFragmentManager, datePIcker.toString())
                }
            }
            else if (binding.buttonEditProfile.text == "Save Profile"){
                if (binding.buttonEditProfile.text == "Save Profile"){
                    val currentUid = auth.currentUser?.uid

                    if (currentUid != null) {
                        val userRef = databaseReference.child("users").child(currentUid)

                        // Update the user's data
                        userRef.child("uname").setValue(binding.usernameEdt.text.toString())
                        userRef.child("fname").setValue(binding.fullnameEdt.text.toString())
                        userRef.child("dbirth").setValue(binding.DoBEdt.text.toString())
                        userRef.child("gender").setValue(binding.genderAuto.text.toString())

                        Toast.makeText(this, "Profile successfully changed", Toast.LENGTH_SHORT).show()

                        binding.genderAuto.isEnabled = false
                        binding.usernameEdt.isClickable = false
                        binding.usernameEdt.isFocusable = false
                        binding.usernameEdt.isFocusableInTouchMode = false  // Set this to true
                        binding.fullnameEdt.isClickable = false
                        binding.fullnameEdt.isFocusable = false
                        binding.fullnameEdt.isFocusableInTouchMode = false  // Set this to true
                        binding.DoBEdt.isClickable = false
                        binding.DoBEdt.isFocusable = false
                        binding.buttonEditProfile.text = "Edit Profile"
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // This ensures the back button works correctly
        return true
    }

    private fun handleDatabaseError(databaseError: DatabaseError) {
        // Handle the database error here (e.g., display a message to the user or log the error)
        Toast.makeText(this, "Database Error: " + databaseError.message, Toast.LENGTH_SHORT).show()
    }
}