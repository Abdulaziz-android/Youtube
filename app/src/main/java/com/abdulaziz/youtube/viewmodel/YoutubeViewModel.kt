package com.abdulaziz.youtube.viewmodel

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abdulaziz.youtube.models.channelbyid.Channel
import com.abdulaziz.youtube.models.popular_videos.PopularVideos
import com.abdulaziz.youtube.models.searchbytext.Item
import com.abdulaziz.youtube.models.video_by_id.VideoById
import com.abdulaziz.youtube.models.videos_by_categoryid.VideosCategoryId
import com.abdulaziz.youtube.retrofit.ApiClient
import com.abdulaziz.youtube.utils.DataSourceSearch
import com.abdulaziz.youtube.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class YoutubeViewModel : ViewModel() {

    private var sPref:SharedPreferences?=null

    private var liveDataPagination:Flow<PagingData<Item>>?=null
    fun getPag(query:String): Flow<PagingData<Item>> {
         liveDataPagination = Pager(PagingConfig(pageSize = 8)){
            DataSourceSearch(ApiClient.apiService, query = query)
        }.flow.cachedIn(viewModelScope)
        return liveDataPagination!!
    }

    private val liveData = MutableLiveData<Resource<PopularVideos>>()

    fun getPopularData(context: Context): LiveData<Resource<PopularVideos>> {
        sPref = context.getSharedPreferences("shared", MODE_PRIVATE)
        val nextKey = sPref!!.getString("key", "")
        viewModelScope.launch {
            liveData.postValue(Resource.loading(null))
            val response = ApiClient.apiService.getPopularVideos(pageToken = nextKey!!)
            if (response.isSuccessful) {
                sPref!!.edit().putString("key",response.body()?.nextPageToken).apply()
                liveData.postValue(Resource.success(response.body()))

            } else liveData.postValue(Resource.error(response.errorBody()?.string().toString(),
                null))
        }
        return liveData
    }

    private val liveDataChannel = MutableLiveData<Channel>()

    fun getDataChannel(text: String): LiveData<Channel> {
        viewModelScope.launch {
            val response = ApiClient.apiService.getChannelById(channel_id = text)
                liveDataChannel.postValue(response)
        }
        return liveDataChannel
    }


    private val liveDataVideoById = MutableLiveData<Resource<VideoById>>()

    fun getDataVideoById(text: String): LiveData<Resource<VideoById>> {
        viewModelScope.launch {
            liveDataVideoById.postValue(Resource.loading(null))
            val response = ApiClient.apiService.getVideoById(video_id = text)
            if (response.isSuccessful) {
                liveDataVideoById.postValue(Resource.success(response.body()))

            } else liveDataVideoById.postValue(Resource.error(response.errorBody()?.string().toString(),
                null))
        }
        return liveDataVideoById
    }


    private val liveDataAll = MutableLiveData<Resource<List<com.abdulaziz.youtube.models.searchedbychannelid.Item>>>()

    fun getSubscribeVideos(channelList: List<String>) : LiveData<Resource<List<com.abdulaziz.youtube.models.searchedbychannelid.Item>>> {
        val list: ArrayList<com.abdulaziz.youtube.models.searchedbychannelid.Item> = arrayListOf()
        liveDataAll.postValue(Resource.loading(null))
        val apiService = ApiClient.apiService
        viewModelScope.launch{
            channelList.forEach {
                val async = async { apiService.getSearchByChannelId(channel_id = it, max_results = 4) }
                val await = async.await()
                await.items.forEach {
                    list.add(it)
                }
            }
            list.shuffle()
            if (list.isNotEmpty())
            liveDataAll.postValue(Resource.success(list))
            else liveDataAll.postValue(Resource.error("Error",
                null))
        }
        return liveDataAll
    }
    private val liveDataVideoCategory = MutableLiveData<Resource<VideosCategoryId>>()

    fun getDataVideosByCategory(categoryId: String): LiveData<Resource<VideosCategoryId>> {
        viewModelScope.launch {
            liveDataVideoCategory.postValue(Resource.loading(null))
            val response = ApiClient.apiService.getVideosByCategoryId(category_id = categoryId)
            if (response.isSuccessful) {
                liveDataVideoCategory.postValue(Resource.success(response.body()))

            } else liveDataVideoCategory.postValue(Resource.error(response.errorBody()?.string().toString(),
                null))
        }
        return liveDataVideoCategory
    }

}