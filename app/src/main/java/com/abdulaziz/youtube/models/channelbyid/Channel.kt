package com.abdulaziz.youtube.models.channelbyid

data class Channel(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
)