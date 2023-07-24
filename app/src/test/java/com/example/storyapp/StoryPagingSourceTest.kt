package com.example.storyapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Call
import retrofit2.Response
import com.example.storyapp.response.StoryResponse

class StoryPagingSourceTest {

    @Test
    fun `test load when successfully loads stories`() = runBlocking {
        val apiService = mock(ApiService::class.java)
        val pagingSource = StoryPagingSource(apiService, "token")
        val page = 0
        val pageSize = 10
        val stories = listOf(Story("1", "Story 1", "Lorem","url1","2022-01-08T06:34:18.598Z",-10.212,-16.002))
        val response = Response.success(StoryResponse(false, "Success", stories))

        val call = mock(Call::class.java) as Call<StoryResponse>
        `when`(apiService.getAllStories("Bearer token", page, pageSize, 0)).thenReturn(call)
        `when`(call.execute()).thenReturn(response)

        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, pageSize, false))

        assertNotNull(result)
        assertEquals(stories.size, (result as PagingSource.LoadResult.Page).data.size)
        assertEquals(stories.first(), (result).data.first())



    }

    @Test
    fun `test load when no stories available`() = runBlocking {
        val apiService = mock(ApiService::class.java)
        val pagingSource = StoryPagingSource(apiService, "token")
        val page = 0
        val pageSize = 10
        val response = Response.success(StoryResponse(false, "Success", emptyList()))

        val call = mock(Call::class.java) as Call<StoryResponse>
        `when`(apiService.getAllStories("Bearer token", page, pageSize, 0)).thenReturn(call)
        `when`(call.execute()).thenReturn(response)

        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, pageSize, false))

        assertNotNull(result)
        assertEquals(0, (result as PagingSource.LoadResult.Page).data.size)
    }

    @Test
    fun `test getRefreshKey returns null`() {
        val apiService = mock(ApiService::class.java)
        val pagingSource = StoryPagingSource(apiService, "token")
        val pagingState = mock(PagingState::class.java) as PagingState<Int, Story>

        val result = pagingSource.getRefreshKey(pagingState)

        assertEquals(null, result)
    }
}

