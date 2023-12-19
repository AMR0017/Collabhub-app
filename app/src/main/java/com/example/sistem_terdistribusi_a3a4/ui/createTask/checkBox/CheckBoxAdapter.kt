package com.example.sistem_terdistribusi_a3a4.ui.createTask.checkBox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import com.example.sistem_terdistribusi_a3a4.R

class CheckBoxAdapter(
    context: Context,
    resource: Int,
    private val items: List<String>,
    private val autoCompleteTextView: MultiAutoCompleteTextView
) : ArrayAdapter<String>(context, resource, items) {

    private val selectedMembers = mutableListOf<String>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.assignee_member_checkbox, parent, false)

        val memberName = getItem(position)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
        val textView = itemView.findViewById<TextView>(R.id.textView)

        checkBox.isChecked = selectedMembers.contains(memberName)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedMembers.add(memberName!!)
                println(memberName)
            }
            if (!isChecked){
                selectedMembers.remove(memberName)
                println(memberName)
            }

            updateAutoCompleteText()
            println(selectedMembers)


        }

        textView.text = memberName

        return itemView
    }

    fun getSelectedMembers(): List<String> {
        return selectedMembers
    }

    private fun updateAutoCompleteText() {
        val selectedText = selectedMembers.joinToString(", ") // Customize the separator as needed
        autoCompleteTextView.setText(selectedText, false)
        autoCompleteTextView.setSelection(autoCompleteTextView.text.length)
    }
}