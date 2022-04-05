package com.abdulaziz.youtube.models.video_by_id

data class Item(
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: Snippet,
    val statistics: Statistics
)