package com.abdulaziz.youtube.models.comments_by_videoid

data class Comments(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
)