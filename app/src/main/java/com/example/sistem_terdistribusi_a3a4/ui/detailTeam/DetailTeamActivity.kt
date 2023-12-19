package com.example.sistem_terdistribusi_a3a4.ui.detailTeam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.data.user.AppObject
import com.example.sistem_terdistribusi_a3a4.data.user.Tasks
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityDetailTeamBinding
import com.example.sistem_terdistribusi_a3a4.ui.createTask.CreateTaskActivity
import com.example.sistem_terdistribusi_a3a4.ui.detailTeam.task.TaskAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class DetailTeamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTeamBinding

    private val handler = Handler()

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var taskAdapter: TaskAdapter
    private var checkedTaskCount = 0


    /*private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("ProgressPrefs", Context.MODE_PRIVATE)
    }

    private var currentProgress: Int
        get() = sharedPreferences.getInt("progress", 0)
        set(value) {
            sharedPreferences.edit().putInt("progress", value).apply()
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios)

        val teamId = AppObject.teamsIdObject

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val currentUid = auth.currentUser?.uid

        if (currentUid != null){
            val teamsRef = databaseReference.child("Teams").child(teamId)

            teamsRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val teamName = snapshot.child("TeamName").value.toString()
                        binding.teamNameDetailTxt.text = teamName
                        AppObject.teamsNameObject = teamName
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error)
                }

            })

        }

        /*binding.taskProgressBar.progress = currentProgress*/


        /*println(currentProgress)*/

        binding.newTaskBtn.setOnClickListener {
            val intent = Intent(this@DetailTeamActivity, CreateTaskActivity::class.java)

            startActivity(intent)
        }


        binding.allButton.setTextColor(getColor(R.color.primary_blue))

        val buttons = listOf<TextView>(binding.allButton,binding.todoButton,binding.doneButton,binding.onProgressButton)
        for (button in buttons){
            button.setOnClickListener(object : View.OnClickListener{
                override fun onClick(view: View?) {
                    for (btn in buttons){
                        btn.setTextColor(getColor(R.color.edt_text_color))
                    }
                    (view as TextView).setTextColor(getColor(R.color.primary_blue))
                }

            })
        }

        taskAdapter = TaskAdapter(emptyList(), databaseReference)
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = taskAdapter

        loadTaskForTeam(teamId)

    }

    private fun loadTaskForTeam(teamId : String){
        val taskRef = databaseReference.child("tasks")

        taskRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasksList = mutableListOf<Tasks>()
                for (taskSnap in snapshot.children){
                    val task = taskSnap.getValue(Tasks::class.java)
                    if (task?.teamsId == teamId){
                        task.let { tasksList.add(it) }
                    }
                }
                taskAdapter.updateTasks(tasksList)
                updateProgressBar(tasksList)
            }

            override fun onCancelled(error: DatabaseError) {
                handleDatabaseError(error)
            }

        })
    }


    private fun updateProgressBar(tasks: List<Tasks>) {
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.taskIsComplete == 1 }
        val progress = if (totalTasks > 0) {
            (completedTasks.toDouble() / totalTasks.toDouble() * 100).toInt()
        } else {
            0
        }

        binding.taskProgressBar.progress = progress
        /*currentProgress = progress*/
        binding.progressPercent.text = "$progress %"
    }


    private fun handleDatabaseError(databaseError: DatabaseError) {
        // Handle the database error here (e.g., display a message to the user or log the error)
        Toast.makeText(this, "Database Error: " + databaseError.message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // This ensures the back button works correctly
        return true
    }
}