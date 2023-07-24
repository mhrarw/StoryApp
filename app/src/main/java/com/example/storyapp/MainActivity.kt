package com.example.storyapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.response.StoryResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preference: PreferenceManager
    private lateinit var apiService: ApiService
    private lateinit var storyAdapter: StoryAdapter

    private val ADD_STORY_REQUEST_CODE = 1

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = PreferenceManager(this)
        apiService = ApiClient.instance.create(ApiService::class.java)

        binding.addStory.setOnClickListener {
            navigateToAddStoryActivity()
        }

        storyAdapter = StoryAdapter({ storyId -> navigateToDetailActivity(storyId) }, { storyAdapter.retry() })
        binding.rvItem.adapter = storyAdapter
        binding.rvItem.layoutManager = LinearLayoutManager(this)

        mainScope.launch {
            withContext(Dispatchers.IO) {
                val pagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = false)
                val storyPagingSource = StoryPagingSource(apiService, preference.getSessionToken())
                val storyPager = Pager(config = pagingConfig, pagingSourceFactory = { storyPagingSource }).flow
                storyPager.collectLatest { pagingData ->
                    storyAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun navigateToAddStoryActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivityForResult(intent, ADD_STORY_REQUEST_CODE)
    }

    private fun navigateToDetailActivity(storyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("storyId", storyId)
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu?.findItem(R.id.log_out)
        menuItem?.setOnMenuItemClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(resources.getString(R.string.log_out))
            dialog.setMessage(getString(R.string.are_you_sure))
            dialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                preference.clearSessionData()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                showMessage(getString(R.string.log_out_warning))
            }
            dialog.setNegativeButton(getString(R.string.no)) { _, _ ->
                showMessage(getString(R.string.not_out))
            }
            dialog.show()
            true
        }
        val mapMenuItem = menu?.findItem(R.id.map)
        mapMenuItem?.setOnMenuItemClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
            true
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_STORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            storyAdapter.refresh()
        }
    }
}