package com.example.storyapp.response

import com.example.storyapp.Story

data class DetailStoryResponse (
    val error: Boolean,
    val message: String,
    val story: Story
)