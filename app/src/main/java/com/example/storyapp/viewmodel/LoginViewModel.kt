package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.ApiClient
import com.example.storyapp.ApiService
import com.example.storyapp.PreferenceManager
import com.example.storyapp.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.storyapp.Result

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Boolean>>()
    val loginResult: LiveData<Result<Boolean>> get() = _loginResult

    private val apiService = ApiClient.instance.create(ApiService::class.java)
    private lateinit var preference: PreferenceManager

    fun setPreference(preference: PreferenceManager) {
        this.preference = preference
    }

    fun loginUser(email: String, password: String) {
        val call = apiService.loginUser(email, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.error) {

                        val userId = loginResponse.loginResult.userId
                        val token = loginResponse.loginResult.token

                        preference.saveSessionData(userId, token)

                        _loginResult.value = Result.Success(true)
                    } else {
                        _loginResult.value = Result.Error("Login failed")
                    }
                } else {
                    _loginResult.value = Result.Error("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = Result.Error("Error: ${t.message}")
            }
        })
    }
}
