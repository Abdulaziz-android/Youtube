package com.abdulaziz.youtube.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abdulaziz.youtube.models.searchbytext.Item
import com.abdulaziz.youtube.retrofit.ApiService

class DataSourceSearch(
    private val apiService: ApiService,
    private val query: String
) :
    PagingSource<String, Item>() {
    override fun getRefreshKey(state: PagingState<String, Item>): String {
        return state.anchorPosition.toString()
    }

    private var page = 0

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Item> {
        return try {
            val nextPageToken = params.key ?: ""
            page++
            val userData = apiService.getSearchByText(query = query, page_token = nextPageToken)
            if (page > 1) {
                LoadResult.Page(userData.body()?.items!!, null, null)
            } else
                LoadResult.Page(
                    userData.body()!!.items,
                    userData.body()!!.prevPageToken,
                    userData.body()!!.nextPageToken
                )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}