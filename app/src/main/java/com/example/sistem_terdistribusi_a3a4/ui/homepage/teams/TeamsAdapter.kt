package com.example.sistem_terdistribusi_a3a4.ui.homepage.teams

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistem_terdistribusi_a3a4.R
import com.example.sistem_terdistribusi_a3a4.data.user.Teams

class TeamsAdapter(private val teams: List<Teams>, private val onItemClick: (Teams) -> Unit) : RecyclerView.Adapter<TeamsAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val teamName : TextView = itemView.findViewById(R.id.teamNameTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_team, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val team = teams[position]
        holder.teamName.text = team.teamName

        holder.itemView.setOnClickListener {
            onItemClick(team)
        }
    }

    override fun getItemCount(): Int {
        return teams.size
    }
}