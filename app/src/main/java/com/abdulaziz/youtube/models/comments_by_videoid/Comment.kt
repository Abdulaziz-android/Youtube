package com.abdulaziz.youtube.models.comments_by_videoid

data class Comment(
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: Snippet
)