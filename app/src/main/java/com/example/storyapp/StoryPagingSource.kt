package com.example.storyapp

import androidx.paging.PagingSource
import androidx.paging.PagingState

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String?
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val call = apiService.getAllStories("Bearer $token", page, pageSize, 0)
            val response = call.execute()
            if (response.isSuccessful) {
                val storyResponse = response.body()
                val stories = storyResponse?.listStory ?: emptyList()
                val nextPage = if (stories.isNotEmpty()) page + 1 else null

                LoadResult.Page(
                    data = stories,
                    prevKey = null,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(Exception("Failed to fetch stories"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return null
    }
}

