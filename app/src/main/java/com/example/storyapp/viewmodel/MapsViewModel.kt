package com.example.storyapp.viewmodel

import android.preference.Preference
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.ApiClient
import com.example.storyapp.ApiService
import com.example.storyapp.PreferenceManager
import com.example.storyapp.Story
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {
    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun fetchStoriesWithLocation(apiService: ApiService, preference: PreferenceManager) {
        viewModelScope.launch {
            try {
                val token = preference.getSessionToken()
                if (token != null) {
                    val response = apiService.getStoriesWithLocation(
                        location = 1,
                        authorization = "Bearer $token"
                    )
                    if (response.isSuccessful) {
                        val storyResponse = response.body()
                        val stories = storyResponse?.listStory
                        _stories.value = stories ?: emptyList()
                    } else {
                        _stories.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
