package com.devmobilejasmin.todo.user

import android.net.Uri
import com.devmobilejasmin.todo.network.Api
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserInfoRepository {
    private val userWebService = Api.userWebService



    suspend fun actualise() : UserInfo?{
        return userWebService.getInfo().body()
    }

    suspend fun changeImage(image: MultipartBody.Part){
        userWebService.updateAvatar(image)
    }

    suspend fun updateTextInfo(userInfo: UserInfo) : UserInfo?{
        return userWebService.update(userInfo).body()
    }



    /*


     */
}