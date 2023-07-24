package com.example.storyapp.response

import com.example.storyapp.Story
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)
