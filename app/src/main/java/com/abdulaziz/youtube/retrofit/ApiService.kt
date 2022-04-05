package com.abdulaziz.youtube.retrofit

import com.abdulaziz.youtube.MainActivity.Companion.API_KEY2
import com.abdulaziz.youtube.models.channelbyid.Channel
import com.abdulaziz.youtube.models.comments_by_videoid.Comments
import com.abdulaziz.youtube.models.popular_videos.PopularVideos
import com.abdulaziz.youtube.models.searchbytext.SearchByTextModel
import com.abdulaziz.youtube.models.searchedbychannelid.SearchedChannelId
import com.abdulaziz.youtube.models.video_by_id.VideoById
import com.abdulaziz.youtube.models.videos_by_categoryid.VideosCategoryId
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getSearchByChannelId(
        @Query("key") key: String = API_KEY2,
        @Query("channelId") channel_id: String,
        @Query("part") part: String = "snippet",
        @Query("order") order: String = "date",
        @Query("maxResults") max_results: Int = 10,
    ): SearchedChannelId

    @GET("channels")
    suspend fun getChannelById(
        @Query("key") key: String = API_KEY2,
        @Query("id") channel_id: String,
        @Query("part") part: String = "snippet",
    ): Channel

    @GET("channels")
    fun getChannelByIdOld(
        @Query("key") key: String = API_KEY2,
        @Query("id") channel_id: String,
        @Query("part") part: String = "snippet",
    ): Call<Channel>

    @GET("search")
    suspend fun getSearchByText(
        @Query("key") key: String = API_KEY2,
        @Query("q") query: String,
        @Query("part") part: String = "snippet",
        @Query("pageToken") page_token: String = "",
        @Query("order") order: String = "relevance",
        @Query("maxResults") max_results: Int = 8,
    ): Response<SearchByTextModel>


    @GET("commentThreads")
    fun getComments(
        @Query("key") key: String = API_KEY2,
        @Query("textFormat") query: String = "plainText",
        @Query("part") part: String = "snippet,replies",
        @Query("order") order: String = "relevance",
        @Query("videoId") video_id: String,
        @Query("maxResults") max_results: Int = 10,
    ): Call<Comments>

    @GET("videos")
    suspend fun getVideoById(
        @Query("part") part: String = "statistics,snippet",
        @Query("id") video_id: String,
        @Query("key") key: String = API_KEY2,
    ): Response<VideoById>

    @GET("videos")
    suspend fun getVideosByCategoryId(
        @Query("chart") video_id: String = "mostPopular",
        @Query("part") part: String = "snippet",
        @Query("videoCategoryId") category_id: String,
        @Query("key") key: String = API_KEY2,
        @Query("maxResults") max_results: Int = 8,
    ): Response<VideosCategoryId>

    @GET("videos")
    suspend fun getPopularVideos(
        @Query("chart") chart: String = "mostPopular",
        @Query("order") order: String = "mostPopular",
        @Query("part") part: String = "snippet",
        @Query("key") key: String = API_KEY2,
        @Query("maxResults") max_results: Int = 14,
        @Query("pageToken") pageToken: String,
    ): Response<PopularVideos>


}