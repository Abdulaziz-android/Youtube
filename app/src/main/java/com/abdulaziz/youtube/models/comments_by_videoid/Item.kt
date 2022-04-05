package com.abdulaziz.youtube.models.comments_by_videoid

data class Item(
    val etag: String,
    val id: String,
    val kind: String,
    val replies: Replies,
    val snippet: SnippetX
)