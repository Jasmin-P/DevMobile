package com.devmobilejasmin.todo.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmobilejasmin.todo.network.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class UserInfoViewModel : ViewModel(){

    private val repository = UserInfoRepository()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    public val userInfo: StateFlow<UserInfo?> = _userInfo


    fun actualise(){
        viewModelScope.launch {
            val newUserInfo = repository.actualise()
            _userInfo.value = newUserInfo
        }
    }

    fun changeImage(image : MultipartBody.Part){
        viewModelScope.launch {
            repository.changeImage(image)
            actualise()
        }

    }

    fun updateTextInfo(userInfo: UserInfo){
        viewModelScope.launch {
            val newUserInfo = repository.updateTextInfo(userInfo)
            _userInfo.value = newUserInfo
            actualise()
        }

    }
}