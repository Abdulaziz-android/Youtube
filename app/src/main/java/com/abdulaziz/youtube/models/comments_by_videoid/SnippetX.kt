package com.abdulaziz.youtube.models.comments_by_videoid

data class SnippetX(
    val canReply: Boolean,
    val isPublic: Boolean,
    val topLevelComment: TopLevelComment,
    val totalReplyCount: Int,
    val videoId: String
)