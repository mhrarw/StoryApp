package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.ApiClient
import com.example.storyapp.ApiService
import com.example.storyapp.PreferenceManager
import com.example.storyapp.Result
import com.example.storyapp.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {

    private val _addStoryResult = MutableLiveData<Result<AddStoryResponse>>()
    val addStoryResult: LiveData<Result<AddStoryResponse>> get() = _addStoryResult

    private val apiService = ApiClient.instance.create(ApiService::class.java)
    private lateinit var preference: PreferenceManager

    fun setPreference(preference: PreferenceManager) {
        this.preference = preference
    }

    fun uploadStory(descriptionPart: RequestBody, photoPart: MultipartBody.Part, latitudePart: RequestBody, longitudePart: RequestBody) {
        val token = preference.getSessionToken()
        if (token != null) {
            val call = apiService.addStory(
                "Bearer $token",
                descriptionPart,
                photoPart,
                latitudePart,
                longitudePart
            )
            call.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                    if (response.isSuccessful) {
                        val addStoryResponse = response.body()
                        if (addStoryResponse != null) {
                            _addStoryResult.value = Result.Success(addStoryResponse)
                        } else {
                            _addStoryResult.value = Result.Error("Failed upload")
                        }
                    } else {
                        _addStoryResult.value = Result.Error("Failed upload")
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _addStoryResult.value = Result.Error("Failed upload")
                }
            })
        } else {
            _addStoryResult.value = Result.Error("Not logged in")
        }
    }
}