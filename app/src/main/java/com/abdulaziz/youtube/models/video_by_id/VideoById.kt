package com.abdulaziz.youtube.models.video_by_id

data class VideoById(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
)