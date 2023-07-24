package com.example.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.response.DetailStoryResponse
import com.example.storyapp.response.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var preference: PreferenceManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"

        preference = PreferenceManager(this)
        apiService = ApiClient.instance.create(ApiService::class.java)

        val storyId = intent.getStringExtra("storyId")
        storyId?.let {
            getStoryDetail(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getStoryDetail(storyId: String) {
        val token = preference.getSessionToken()

        if (token != null) {
            val call = apiService.getStoryDetail("Bearer $token", storyId)

            call.enqueue(object : Callback<DetailStoryResponse> {
                override fun onResponse(
                    call: Call<DetailStoryResponse>,
                    response: Response<DetailStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val storyResponse = response.body()
                        if (storyResponse != null && !storyResponse.error) {
                            val story = storyResponse.story
                            if (story != null) {
                                bindStoryData(story)
                            } else {
                                Toast.makeText(this@DetailActivity,
                                    "Story not found",
                                    Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DetailActivity,
                                "Failed to get story detail",
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@DetailActivity,
                            "Failed to get story detail",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    Toast.makeText(this@DetailActivity,
                        "Failed to get story detail",
                        Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindStoryData(story: Story) {
        binding.tvDetailName.text = story.name
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailDescription.text = story.description
    }
}