package com.devmobilejasmin.todo.network

import com.devmobilejasmin.todo.user.LoginForm
import com.devmobilejasmin.todo.user.LoginResponse
import com.devmobilejasmin.todo.user.SignUpForm
import com.devmobilejasmin.todo.user.UserInfo
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>


    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @PATCH("users")
    suspend fun update(@Body user: UserInfo): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body user: LoginForm): Response<LoginResponse>

    @POST("users/sign_up")
    suspend fun singUp(@Body user: SignUpForm): Response<LoginResponse>
}