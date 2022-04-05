package com.abdulaziz.youtube.models.videos_by_categoryid

data class VideosCategoryId(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
)