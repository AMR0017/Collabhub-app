package com.example.sistem_terdistribusi_a3a4.ui.detailTeam.task

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.data.user.Tasks
import com.example.sistem_terdistribusi_a3a4.databinding.ItemRowTaskBinding
import com.example.sistem_terdistribusi_a3a4.utils.other.calculateRemainingDays
import com.google.firebase.database.DatabaseReference
import org.w3c.dom.Text

class TaskAdapter(private var tasks: List<Tasks>,private val databaseReference: DatabaseReference) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val taskName :  TextView = itemView.findViewById(R.id.taskNametxt)
        val teamName : TextView = itemView.findViewById(R.id.teamNameTxt)
        val checkBox : CheckBox = itemView.findViewById(R.id.roundCheckbox)
        val date: TextView = itemView.findViewById(R.id.dateTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.tasksName
        holder.teamName.text = task.teamsName


        val remainingDays = calculateRemainingDays(task.taskEndDate)

        val dueDateText = if (remainingDays > 0){
            "$remainingDays days remaining"
        }else{
            "Due on ${task.taskEndDate}"
        }
        holder.date.text = dueDateText

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.taskIsComplete == 1

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // Update the task object
            val updatedTask = task.copy(taskIsComplete = if (isChecked) 1 else 0)

            // Update the task in the list
            val updatedTasks = tasks.toMutableList().apply { set(position, updatedTask) }

            // Update the task in Firebase
            updateTaskInFirebase(updatedTask)

            // Update the adapter with the new list
            updateTasks(updatedTasks)
        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    private fun updateTaskInFirebase(updatedTask: Tasks) {
        val tasksReference = databaseReference.child("tasks")

        // Use the taskId to update the specific task
        tasksReference.child(updatedTask.taskId).setValue(updatedTask)
            .addOnSuccessListener {
                // Handle success, if needed
            }
            .addOnFailureListener {
                // Handle failure, if needed
            }
    }

    fun updateTasks(newTasks: List<Tasks>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}