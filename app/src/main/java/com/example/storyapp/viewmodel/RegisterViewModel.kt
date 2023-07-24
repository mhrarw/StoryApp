package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.ApiClient
import com.example.storyapp.ApiService
import com.example.storyapp.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.storyapp.Result

class RegisterViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> get() = _registerResult

    private val apiService = ApiClient.instance.create(ApiService::class.java)

    fun registerUser(name: String, email: String, password: String) {
        val call = apiService.registerUser(name, email, password)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && !registerResponse.error) {
                        _registerResult.value = Result.Success(registerResponse)
                    } else {
                        _registerResult.value = Result.Error("Registration failed")
                    }
                } else {
                    _registerResult.value = Result.Error("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerResult.value = Result.Error("Error: ${t.message}")
            }
        })
    }
}
