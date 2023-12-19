package com.example.sistem_terdistribusi_a3a4.ui.homepage

import android.app.Dialog
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.data.user.AppObject
import com.example.sistem_terdistribusi_a3a4.data.user.Tasks
import com.example.sistem_terdistribusi_a3a4.data.user.Teams
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityHomepageBinding
import com.example.sistem_terdistribusi_a3a4.ui.createTeam.CreateTeamActivity
import com.example.sistem_terdistribusi_a3a4.ui.detailTeam.DetailTeamActivity
import com.example.sistem_terdistribusi_a3a4.ui.detailTeam.task.TaskAdapter
import com.example.sistem_terdistribusi_a3a4.ui.homepage.teams.TeamsAdapter
import com.example.sistem_terdistribusi_a3a4.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import org.w3c.dom.Text

class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var teamsAdapter: TeamsAdapter
    private val teamList : MutableList<Teams> = ArrayList()
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

        showTeamRecyclerView()

        binding.btnAddTeam.setOnClickListener {
            showPopUpChooserDialog()
        }

        binding.imageView2.setOnClickListener{
            val intent = Intent(this@HomepageActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showTeamRecyclerView(){
        val recyclerView : RecyclerView = findViewById(R.id.teamRV)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        teamsAdapter = TeamsAdapter(teamList) {teams ->
            val intent = Intent(this@HomepageActivity, DetailTeamActivity::class.java)
            intent.putExtra("teamId", teams.teamId)
            AppObject.teamsIdObject = teams.teamId
            startActivity(intent)
        }
        recyclerView.adapter = teamsAdapter

        databaseReference = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid
        val loading = findViewById<ProgressBar>(R.id.teamProgressBar)

        loading.visibility = View.VISIBLE

        databaseReference.child("Teams").orderByChild("members/$userId").equalTo(true)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    teamList.clear()
                    for (teamSnapshot in snapshot.children){
                        val teamName = teamSnapshot.child("TeamName").value.toString()
                        val teamInvCode = teamSnapshot.child("InvCode").value.toString()
                        val teamId = teamSnapshot.key.toString()
                        teamList.add(Teams(teamId, teamName,teamInvCode))
                    }

                    teamsAdapter.notifyDataSetChanged()

                    val teamCount = snapshot.childrenCount.toInt()
                    val teamNums = findViewById<TextView>(R.id.teamsCount)
                    teamNums.text = teamCount.toString()
                    loading.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                    loading.visibility = View.GONE
                }

            })
    }

    private fun showPopUpChooserDialog(){
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val cardView = layoutInflater.inflate(R.layout.item_addteamchooser, null)
        dialog.setContentView(cardView)

        val joinTeamButton = dialog.findViewById<Button>(R.id.btnJoinTeam)
        val newTeamButton = dialog.findViewById<Button>(R.id.btnNewTeam)

        newTeamButton.setOnClickListener {
            val intent = Intent(this, CreateTeamActivity::class.java)
            startActivity(intent)
            dialog.dismiss() // Close the dialog when starting a new activity
        }

        joinTeamButton.setOnClickListener {
            showJoinTeamPopUpDialog()
            dialog.dismiss() // Close the dialog when showing a toast
        }

        val window = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.CENTER)
        dialog.show()
    }



    private fun showJoinTeamPopUpDialog(){
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val cardView = layoutInflater.inflate(R.layout.item_jointeam, null)
        dialog.setContentView(cardView)

        val joinTeamButton = dialog.findViewById<Button>(R.id.joinTeamBtn)
        val cancel = dialog.findViewById<Button>(R.id.cancelJoinBtn)
        val invitationCodeEdt = dialog.findViewById<EditText>(R.id.invCodeEdt)

        val pasteImg = dialog.findViewById<ImageView>(R.id.pasteBtn)

        pasteImg.setOnClickListener {
            val clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipManager.primaryClip
            if (clipData != null && clipData.itemCount > 0){
                val pastedText = clipData.getItemAt(0).text.toString()
                invitationCodeEdt.setText(pastedText)
            }
        }
        joinTeamButton.setOnClickListener {
            val invCode = invitationCodeEdt.text.toString()
            val teamQuery = databaseReference.child("Teams").orderByChild("InvCode").equalTo(invCode)
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            teamQuery.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val teamId = snapshot.children.first().key
                        val teamRef = teamId?.let { it1 ->
                            databaseReference.child("Teams").child(
                                it1
                            )
                        }
                        val userUpdates = hashMapOf<String, Any>()
                        userUpdates["members/$userId"] = true

                        teamRef?.updateChildren(userUpdates)
                    }else{
                        Toast.makeText(applicationContext, "Invitation Code Invalid, Check and try again",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Error, Please Try again later", Toast.LENGTH_SHORT).show()
                }

            })
            dialog.dismiss() // Close the dialog when starting a new activity
        }


        cancel.setOnClickListener {
            dialog.dismiss() // Close the dialog when showing a toast
        }

        val window = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.CENTER)
        dialog.show()
    }

    private fun handleDatabaseError(databaseError: DatabaseError) {
        if (auth.currentUser != null) {
            // User is still authenticated, show the error
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
            Toast.makeText(this@HomepageActivity, errorMessage, Toast.LENGTH_LONG).show()

            // You can also log the error for debugging purposes
            Log.e("FirebaseDatabaseError", errorMessage)
        }
    }

}