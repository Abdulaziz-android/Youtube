package com.abdulaziz.youtube.models.comments_by_videoid

data class SnippetXX(
    val authorChannelId: AuthorChannelIdX,
    val authorChannelUrl: String,
    val authorDisplayName: String,
    val authorProfileImageUrl: String,
    val canRate: Boolean,
    val likeCount: Int,
    val publishedAt: String,
    val textDisplay: String,
    val textOriginal: String,
    val updatedAt: String,
    val videoId: String,
    val viewerRating: String
)