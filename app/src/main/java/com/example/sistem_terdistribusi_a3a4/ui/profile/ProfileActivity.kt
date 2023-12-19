package com.example.sistem_terdistribusi_a3a4.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityProfileBinding
import com.example.sistem_terdistribusi_a3a4.ui.onboarding.OnboardingActivity
import com.example.sistem_terdistribusi_a3a4.ui.profile.subprofile.ChangePasswordActivity
import com.example.sistem_terdistribusi_a3a4.ui.profile.subprofile.EditProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val currentUid = auth.currentUser?.uid

        if (currentUid != null){
            val userRef = databaseReference.child("users").child(currentUid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val username = snapshot.child("uname").value.toString()
                        val profilePic = snapshot.child("profilePic").value.toString()
                        val email = snapshot.child("email").value.toString()

                        binding.usernameTV.text = username
                        binding.emailTv.text = email

                        Glide.with(this@ProfileActivity)
                            .load(profilePic)
                            .circleCrop()
                            .into(binding.imageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                }
            })
        }

        binding.logOut.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(this)
            val bottomSheetView = layoutInflater.inflate(R.layout.item_bottom_sheet_logout, null)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            // Menghubungkan listener untuk tombol-tombol di dalam bottom sheet
            bottomSheetView.findViewById<View>(R.id.btnBatal)?.setOnClickListener(bottomSheetClickListener)
            bottomSheetView.findViewById<View>(R.id.btnKeluar)?.setOnClickListener(bottomSheetClickListener)
        }

        binding.editProfile.setOnClickListener {
            val intent =  Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePass.setOnClickListener {
            val intent =  Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        binding.policy.setOnClickListener {
            Toast.makeText(this, "This feature does not implemented yet", Toast.LENGTH_SHORT).show()
        }
        binding.helpCenter.setOnClickListener {
            Toast.makeText(this, "This feature does not implemented yet", Toast.LENGTH_SHORT).show()
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

    private val bottomSheetClickListener = View.OnClickListener { view ->
        when (view?.id) {
            R.id.btnBatal -> {
                bottomSheetDialog.dismiss()
            }
            R.id.btnKeluar -> {
                auth.signOut()
                Toast.makeText(this, "Sign out successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}