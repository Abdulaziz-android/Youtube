package com.abdulaziz.youtube.models.channelbyid

data class Item(
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: Snippet
)