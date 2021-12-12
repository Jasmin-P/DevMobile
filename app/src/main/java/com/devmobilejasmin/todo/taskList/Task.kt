package com.devmobilejasmin.todo.taskList

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task (
    @SerialName("id")
    val id : String,
    @SerialName("title")
    val title : String,
    @SerialName("description")
    val description : String = "default description"
) : java.io.Serializable