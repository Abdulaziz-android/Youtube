package com.abdulaziz.youtube.models.searchbytext

data class SearchByTextModel(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val nextPageToken: String,
    val prevPageToken: String,
    val pageInfo: PageInfo,
    val regionCode: String
)