package com.abdulaziz.youtube.models.searchedbychannelid

data class SearchedChannelId(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo,
    val regionCode: String
)