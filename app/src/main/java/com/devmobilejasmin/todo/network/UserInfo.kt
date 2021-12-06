package com.devmobilejasmin.todo.network

import kotlinx.serialization.SerialName


data class UserInfo(
    @SerialName("email")
    val email: String,
    @SerialName("firstname")
    val firstName: String,
    @SerialName("lastname")
    val lastName: String
)

