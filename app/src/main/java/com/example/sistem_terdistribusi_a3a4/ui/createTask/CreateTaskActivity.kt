package com.example.sistem_terdistribusi_a3a4.ui.createTask

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.data.user.AppObject
import com.example.sistem_terdistribusi_a3a4.data.user.Tasks
import com.example.sistem_terdistribusi_a3a4.databinding.ActivityCreateTaskBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

        showDatePicker()

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.createTaskBtn.setOnClickListener {
            saveTask()
        }

    }

    private fun saveTask(){
        val taskName = binding.tasknameEdt.text.toString()
        val startDate = binding.startDateEdt.text.toString()
        val endDate = binding.endDateEdt.text.toString()

        val currentUID = auth.currentUser?.uid
        val teamId = AppObject.teamsIdObject
        var teamsName = AppObject.teamsNameObject


        val task = Tasks(
            taskId = UUID.randomUUID().toString(),
            teamsId = teamId,
            teamsName = teamsName,
            tasksName = taskName,
            taskStartDate = startDate,
            taskEndDate = endDate
        )

        databaseReference.child("tasks").child(task.taskId).setValue(task)
        finish()
    }

    private fun showDatePicker(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select your date of birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        var endStart = 0

        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            if (endStart == 0){
                binding.startDateEdt.setText(sdf.format(it))
            }else{
                binding.endDateEdt.setText(sdf.format(it))
            }

        }

        binding.startDateEdt.setOnClickListener {
            endStart = 0
            datePicker.show(supportFragmentManager, datePicker.toString())
        }

        binding.startDateLayoutEdt.setEndIconOnClickListener{
            endStart = 0
            datePicker.show(supportFragmentManager, datePicker.toString())
        }

        binding.endDateEdt.setOnClickListener{
            endStart = 1
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
        binding.endDateLayoutEdt.setEndIconOnClickListener {
            endStart = 1
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // This ensures the back button works correctly
        return true
    }

}