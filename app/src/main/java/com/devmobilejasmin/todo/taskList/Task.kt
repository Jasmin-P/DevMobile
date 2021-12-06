package com.devmobilejasmin.todo.taskList


data class Task (val id : String, val title : String, val description : String = "default description") : java.io.Serializable