package com.abdulaziz.youtube.models.searchedbychannelid

data class Item(
    val etag: String,
    val id: Id,
    val kind: String,
    val snippet: Snippet
)