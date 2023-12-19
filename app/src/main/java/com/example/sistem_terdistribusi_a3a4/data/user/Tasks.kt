package com.example.sistem_terdistribusi_a3a4.data.user

data class Tasks (
    val taskId : String,
    val teamsId: String,
    val teamsName: String,
    val tasksName: String,
    val taskStartDate: String,
    val taskEndDate: String,
    var taskIsComplete: Int = 0
){
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", "", "", 0)
}