package com.example.sistem_terdistribusi_a3a4.ui.homepage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityHomepageBinding
import com.example.sistem_terdistribusi_a3a4.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val currentUid = auth.currentUser?.uid

        if (currentUid != null){
            val userRef = databaseReference.child("users").child(currentUid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val username = snapshot.child("uname").value.toString()
                        val profilePic = snapshot.child("profilePic").value.toString()
                        binding.UsernameText.text = "Hello $username,"

                        Glide.with(this@HomepageActivity)
                            .load(profilePic)
                            .circleCrop()
                            .into(binding.imageView2)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                }
            })
        }

        binding.imageView2.setOnClickListener{
            val intent = Intent(this@HomepageActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun handleDatabaseError(databaseError: DatabaseError) {
        // Handle the database error here (e.g., display a message to the user or log the error)
        Toast.makeText(this, "Database Error: " + databaseError.message, Toast.LENGTH_SHORT).show()
    }
}