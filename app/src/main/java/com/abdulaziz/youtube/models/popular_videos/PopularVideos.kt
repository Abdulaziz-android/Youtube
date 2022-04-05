package com.abdulaziz.youtube.models.popular_videos

data class PopularVideos(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
)