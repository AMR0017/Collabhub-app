package com.example.sistem_terdistribusi_a3a4.ui.createTeam

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityCreateTeamBinding
import com.example.sistem_terdistribusi_a3a4.ui.homepage.HomepageActivity
import com.example.sistem_terdistribusi_a3a4.utils.other.generateInvitationCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateTeamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTeamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

        val databaseReference = FirebaseDatabase.getInstance().reference
        val userid = FirebaseAuth.getInstance().currentUser?.uid

        var isButtonClicked = false

        binding.generateInvId.setOnClickListener {
            if (!isButtonClicked) {
                isButtonClicked = true
                val teamInvId = generateInvitationCode()
                binding.invcodeTxt.text = teamInvId
                binding.alertCopy.visibility = View.VISIBLE
                binding.copyBtn.visibility = View.VISIBLE

                binding.copyBtn.setOnClickListener {
                    val textCopy = binding.invcodeTxt.text
                    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("label", textCopy)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(this, "Invite Code Copied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.createTeamBtn.setOnClickListener {
            val teamName = binding.teamnameEdt.text.toString()
            val invCode = binding.invcodeTxt.text.toString()

            if (teamName.isEmpty()){
                Toast.makeText(this, "Fill the Team Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (invCode.isEmpty()){
                Toast.makeText(this, "Generate the Invite Code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userid != null){
                val teamRef = databaseReference.child("Teams").push()
                val teamId = teamRef.key
                val teamData = mapOf(
                    "TeamName" to teamName,
                    "members" to mapOf(userid to true),
                    "InvCode" to invCode
                )
                teamRef.setValue(teamData)

                val intent = Intent(this@CreateTeamActivity, HomepageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }



    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // This ensures the back button works correctly
        return true
    }
}